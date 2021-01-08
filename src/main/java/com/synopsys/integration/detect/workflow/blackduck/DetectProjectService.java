/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.generated.view.TagView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.http.BlackDuckRequestFilter;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectMappingService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectUsersService;
import com.synopsys.integration.blackduck.service.dataservice.TagService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class DetectProjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final DetectProjectServiceOptions detectProjectServiceOptions;
    private final ProjectMappingService projectMappingService;
    private final DetectCustomFieldService detectCustomFieldService;

    public DetectProjectService(BlackDuckServicesFactory blackDuckServicesFactory, DetectProjectServiceOptions detectProjectServiceOptions, ProjectMappingService projectMappingService,
        DetectCustomFieldService detectCustomFieldService) {
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.detectProjectServiceOptions = detectProjectServiceOptions;
        this.projectMappingService = projectMappingService;
        this.detectCustomFieldService = detectCustomFieldService;
    }

    public ProjectVersionWrapper createOrUpdateBlackDuckProject(NameVersion projectNameVersion) throws IntegrationException, DetectUserFriendlyException {
        ProjectService projectService = blackDuckServicesFactory.createProjectService();
        BlackDuckApiClient blackDuckService = blackDuckServicesFactory.getBlackDuckApiClient();
        ProjectSyncModel projectSyncModel = createProjectSyncModel(projectNameVersion);
        boolean forceUpdate = detectProjectServiceOptions.isForceProjectVersionUpdate();
        ProjectVersionWrapper projectVersionWrapper = projectService.syncProjectAndVersion(projectSyncModel, forceUpdate);

        ProjectBomService projectBomService = blackDuckServicesFactory.createProjectBomService();
        mapToParentProjectVersion(blackDuckService, projectService, projectBomService, detectProjectServiceOptions.getParentProjectName(), detectProjectServiceOptions.getParentProjectVersion(), projectVersionWrapper);

        setApplicationId(projectVersionWrapper.getProjectView(), detectProjectServiceOptions.getApplicationId());
        CustomFieldDocument customFieldDocument = detectProjectServiceOptions.getCustomFields();
        if (customFieldDocument != null && (customFieldDocument.getProject().size() > 0 || customFieldDocument.getVersion().size() > 0)) {
            logger.debug("Will update the following custom fields and values.");
            for (CustomFieldElement element : customFieldDocument.getProject()) {
                logger.debug(String.format("Project field '%s' will be set to '%s'.", element.getLabel(), String.join(",", element.getValue())));
            }
            for (CustomFieldElement element : customFieldDocument.getVersion()) {
                logger.debug(String.format("Version field '%s' will be set to '%s'.", element.getLabel(), String.join(",", element.getValue())));
            }

            detectCustomFieldService.updateCustomFields(projectVersionWrapper, customFieldDocument, blackDuckServicesFactory.getBlackDuckApiClient());
            logger.info("Successfully updated (" + (customFieldDocument.getVersion().size() + customFieldDocument.getProject().size()) + ") custom fields.");
        } else {
            logger.debug("No custom fields to set.");
        }

        ProjectUsersService projectUsersService = blackDuckServicesFactory.createProjectUsersService();
        TagService tagService = blackDuckServicesFactory.createTagService();
        addUserGroupsToProject(projectUsersService, projectVersionWrapper, detectProjectServiceOptions.getGroups());
        addTagsToProject(tagService, projectVersionWrapper, detectProjectServiceOptions.getTags());
        return projectVersionWrapper;
    }

    private void mapToParentProjectVersion(BlackDuckApiClient blackDuckService, ProjectService projectService, ProjectBomService projectBomService, String parentProjectName, String parentVersionName,
        ProjectVersionWrapper projectVersionWrapper)
        throws DetectUserFriendlyException {
        if (StringUtils.isNotBlank(parentProjectName) || StringUtils.isNotBlank(parentVersionName)) {
            logger.debug("Will attempt to add this project to a parent.");
            String projectName = projectVersionWrapper.getProjectView().getName();
            String projectVersionName = projectVersionWrapper.getProjectVersionView().getVersionName();
            if (StringUtils.isBlank(parentProjectName) || StringUtils.isBlank(parentVersionName)) {
                throw new DetectUserFriendlyException("Both the parent project name and the parent project version name must be specified if either is specified.", ExitCodeType.FAILURE_CONFIGURATION);
            }
            try {
                Optional<ProjectVersionWrapper> parentWrapper = projectService.getProjectVersion(parentProjectName, parentVersionName);
                if (parentWrapper.isPresent()) {
                    ProjectVersionView parentProjectVersionView = parentWrapper.get().getProjectVersionView();
                    BlackDuckRequestBuilder requestBuilder = BlackDuckServicesFactory.createDefaultRequestFactory().createCommonGetRequestBuilder();
                    requestBuilder.addBlackDuckFilter(BlackDuckRequestFilter.createFilterWithSingleValue("bomComponentSource", "custom_project"));
                    List<ProjectVersionComponentView> components = blackDuckService.getAllResponses(parentProjectVersionView, ProjectVersionView.COMPONENTS_LINK_RESPONSE, requestBuilder);
                    Optional<ProjectVersionComponentView> existingProjectComponent = components.stream()
                                                                                         .filter(component -> component.getComponentName().equals(projectName))
                                                                                         .filter(component -> component.getComponentVersionName().equals(projectVersionName))
                                                                                         .findFirst();
                    if (existingProjectComponent.isPresent()) {
                        logger.debug("This project already exists on the parent so it will not be added to the parent again.");
                    } else {
                        projectBomService.addProjectVersionToProjectVersion(projectVersionWrapper.getProjectVersionView(), parentWrapper.get().getProjectVersionView());
                    }
                } else {
                    throw new DetectUserFriendlyException("Unable to find parent project or parent project version on the server.", ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
                }
            } catch (IntegrationException e) {
                throw new DetectUserFriendlyException("Unable to add project to parent.", e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }

    }

    private void addUserGroupsToProject(ProjectUsersService projectUsersService, ProjectVersionWrapper projectVersionWrapper, List<String> groupsToAddToProject) throws IntegrationException {
        if (groupsToAddToProject == null) {
            return;
        }
        for (String userGroupName : groupsToAddToProject) {
            if (StringUtils.isNotBlank(userGroupName)) {
                logger.debug(String.format("Adding user group %s to project %s", userGroupName, projectVersionWrapper.getProjectView().getName()));
                projectUsersService.addGroupToProject(projectVersionWrapper.getProjectView(), userGroupName);
            }
        }
    }

    private void addTagsToProject(TagService tagService, ProjectVersionWrapper projectVersionWrapper, List<String> tags) throws IntegrationException {
        if (tags == null) {
            return;
        }
        List<String> validTags = tags.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (validTags.size() > 0) {
            List<TagView> currentTags = tagService.getAllTags(projectVersionWrapper.getProjectView());
            for (String tag : validTags) {
                boolean currentTagExists = currentTags.stream().anyMatch(tagView -> tagView.getName().equalsIgnoreCase(tag));
                if (!currentTagExists) {
                    logger.debug(String.format("Adding tag %s to project %s", tag, projectVersionWrapper.getProjectView().getName()));
                    TagView tagView = new TagView();
                    tagView.setName(tag);
                    tagService.createTag(projectVersionWrapper.getProjectView(), tagView);
                } else {
                    logger.debug(String.format("Skipping tag as it already exists %s", tag));
                }
            }
        }
    }

    public ProjectSyncModel createProjectSyncModel(NameVersion projectNameVersion) throws DetectUserFriendlyException {
        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectNameVersion.getName(), projectNameVersion.getVersion());

        // TODO: Handle a boolean property not being set in detect configuration - ie need to determine if this property actually exists in the ConfigurableEnvironment - just omit this one?
        projectSyncModel.setProjectLevelAdjustments(detectProjectServiceOptions.isProjectLevelAdjustments());

        Optional.ofNullable(detectProjectServiceOptions.getProjectVersionPhase()).ifPresent(projectSyncModel::setPhase);
        Optional.ofNullable(detectProjectServiceOptions.getProjectVersionDistribution()).ifPresent(projectSyncModel::setDistribution);

        Integer projectTier = detectProjectServiceOptions.getProjectTier();
        if (null != projectTier && projectTier >= 1 && projectTier <= 5) {
            projectSyncModel.setProjectTier(projectTier);
        }

        String description = detectProjectServiceOptions.getProjectDescription();
        if (StringUtils.isNotBlank(description)) {
            projectSyncModel.setDescription(description);
        }

        String releaseComments = detectProjectServiceOptions.getProjectVersionNotes();
        if (StringUtils.isNotBlank(releaseComments)) {
            projectSyncModel.setReleaseComments(releaseComments);
        }

        List<ProjectCloneCategoriesType> cloneCategories = detectProjectServiceOptions.getCloneCategories();
        if (!cloneCategories.isEmpty()) {
            projectSyncModel.setCloneCategories(cloneCategories);
        }

        String nickname = detectProjectServiceOptions.getProjectVersionNickname();
        if (StringUtils.isNotBlank(nickname)) {
            projectSyncModel.setNickname(nickname);
        }

        Optional<HttpUrl> cloneUrl = findCloneUrl(projectNameVersion.getName()); //TODO: Be passed the clone url.
        if (cloneUrl.isPresent()) {
            logger.debug("Cloning project version from release url: " + cloneUrl.get());
            projectSyncModel.setCloneFromReleaseUrl(cloneUrl.get().string());
        }

        return projectSyncModel;
    }

    public Optional<HttpUrl> findCloneUrl(String projectName) throws DetectUserFriendlyException {
        if (detectProjectServiceOptions.getCloneLatestProjectVersion()) {
            logger.debug("Cloning the most recent project version.");
            return findLatestProjectVersionCloneUrl(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createProjectService(), projectName);
        } else if (StringUtils.isNotBlank(detectProjectServiceOptions.getCloneVersionName())) {
            return findNamedCloneUrl(projectName, detectProjectServiceOptions.getCloneVersionName(), blackDuckServicesFactory.createProjectService());
        } else {
            logger.debug("No clone project or version name supplied. Will not clone.");
            return Optional.empty();
        }
    }

    public Optional<HttpUrl> findNamedCloneUrl(String cloneProjectName, String cloneProjectVersionName, ProjectService projectService) throws DetectUserFriendlyException {
        try {
            Optional<ProjectVersionWrapper> projectVersionWrapper = projectService.getProjectVersion(cloneProjectName, cloneProjectVersionName);
            if (projectVersionWrapper.isPresent()) {
                return Optional.of(projectVersionWrapper.get().getProjectVersionView().getHref());
            } else {
                logger.warn(String.format("Project/version %s/%s not found for cloning", cloneProjectName, cloneProjectVersionName));
                return Optional.empty();
            }
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("Error finding project/version (%s/%s) to clone, or getting its release url.", cloneProjectName, cloneProjectVersionName), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public Optional<HttpUrl> findLatestProjectVersionCloneUrl(BlackDuckApiClient blackDuckService, ProjectService projectService, String projectName) throws DetectUserFriendlyException {
        try {
            Optional<ProjectView> projectView = projectService.getProjectByName(projectName);
            if (projectView.isPresent()) {
                List<ProjectVersionView> projectVersionViews = blackDuckService.getAllResponses(projectView.get(), ProjectView.VERSIONS_LINK_RESPONSE);
                if (projectVersionViews.isEmpty()) {
                    logger.warn("Could not find an existing project version to clone from. Ensure the project exists when using the latest clone flag.");
                    return Optional.empty();
                } else {
                    return projectVersionViews.stream()
                               .max(Comparator.comparing(ProjectVersionView::getCreatedAt))
                               .map(BlackDuckView::getHref);
                }
            } else {
                logger.warn("Could not find existing project to clone from. Ensure the project exists when using the latest clone flag.");
                return Optional.empty();
            }
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException("Error finding latest version to clone, or getting its release url.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public void setApplicationId(ProjectView projectView, String applicationId) throws DetectUserFriendlyException {
        if (StringUtils.isBlank(applicationId)) {
            logger.debug("No 'Application ID' to set.");
            return;
        }

        try {
            logger.debug("Populating project 'Application ID'");
            projectMappingService.populateApplicationId(projectView, applicationId);
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("Unable to set Application ID for project: %s", projectView.getName()), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}


