/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.generated.view.TagView;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectBomService;
import com.synopsys.integration.blackduck.service.ProjectMappingService;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ProjectUsersService;
import com.synopsys.integration.blackduck.service.TagService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class DetectProjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final DetectProjectServiceOptions detectProjectServiceOptions;
    private final ProjectMappingService projectMappingService;

    public DetectProjectService(final BlackDuckServicesFactory blackDuckServicesFactory, final DetectProjectServiceOptions detectProjectServiceOptions, final ProjectMappingService projectMappingService) {
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.detectProjectServiceOptions = detectProjectServiceOptions;
        this.projectMappingService = projectMappingService;
    }

    public ProjectVersionWrapper createOrUpdateBlackDuckProject(final NameVersion projectNameVersion) throws IntegrationException, DetectUserFriendlyException {
        final ProjectService projectService = blackDuckServicesFactory.createProjectService();
        final ProjectSyncModel projectSyncModel = createProjectSyncModel(projectNameVersion);
        final boolean forceUpdate = detectProjectServiceOptions.isForceProjectVersionUpdate();
        final ProjectVersionWrapper projectVersionWrapper = projectService.syncProjectAndVersion(projectSyncModel, forceUpdate);

        final ProjectBomService projectBomService = blackDuckServicesFactory.createProjectBomService();
        mapToParentProjectVersion(projectService, projectBomService, detectProjectServiceOptions.getParentProjectName(), detectProjectServiceOptions.getParentProjectVersion(), projectVersionWrapper);

        setApplicationId(projectVersionWrapper.getProjectView(), detectProjectServiceOptions.getApplicationId());
        final ProjectUsersService projectUsersService = blackDuckServicesFactory.createProjectUsersService();
        final TagService tagService = blackDuckServicesFactory.createTagService();
        addUserGroupsToProject(projectUsersService, projectVersionWrapper, detectProjectServiceOptions.getGroups());
        addTagsToProject(tagService, projectVersionWrapper, detectProjectServiceOptions.getTags());
        return projectVersionWrapper;
    }

    private void mapToParentProjectVersion(ProjectService projectService, ProjectBomService projectBomService, String parentProjectName, String parentVersionName, ProjectVersionWrapper projectVersionWrapper)
        throws DetectUserFriendlyException {
        if (StringUtils.isNotBlank(parentProjectName) || StringUtils.isNotBlank(parentVersionName)) {
            logger.info("Will attempt to add this project to a parent.");
            if (StringUtils.isBlank(parentProjectName) || StringUtils.isBlank(parentVersionName)) {
                throw new DetectUserFriendlyException("Both the parent project name and the parent project version name must be specified if either is specified.", ExitCodeType.FAILURE_CONFIGURATION);
            }
            try {
                Optional<ProjectVersionWrapper> parentWrapper = projectService.getProjectVersion(parentProjectName, parentVersionName);
                if (parentWrapper.isPresent()) {
                    String componentLink = parentWrapper.get().getProjectVersionView().getFirstLink(ProjectVersionView.COMPONENTS_LINK).orElse(null);
                    String projectLink = projectVersionWrapper.getProjectVersionView().getHref().get();
                    projectBomService.addComponentToProjectVersion("application/json", componentLink, projectLink);
                } else {
                    throw new DetectUserFriendlyException("Unable to find parent project or parent project version on the server.", ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
                }
            } catch (IntegrationException e) {
                throw new DetectUserFriendlyException("Unable to add project to parent.", e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
            }
        }

    }

    private void addUserGroupsToProject(final ProjectUsersService projectUsersService, final ProjectVersionWrapper projectVersionWrapper, final String[] groupsToAddToProject) throws IntegrationException {
        if (groupsToAddToProject == null) {
            return;
        }
        for (final String userGroupName : groupsToAddToProject) {
            if (StringUtils.isNotBlank(userGroupName)) {
                logger.debug(String.format("Adding user group %s to project %s", userGroupName, projectVersionWrapper.getProjectView().getName()));
                projectUsersService.addGroupToProject(projectVersionWrapper.getProjectView(), userGroupName);
            }
        }
    }

    private void addTagsToProject(final TagService tagService, final ProjectVersionWrapper projectVersionWrapper, final String[] tags) throws IntegrationException {
        if (tags == null) {
            return;
        }
        List<String> validTags = Arrays.stream(tags).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (validTags.size() > 0) {
            List<TagView> currentTags = tagService.getAllTags(projectVersionWrapper.getProjectView());
            for (final String tag : validTags) {
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

    public ProjectSyncModel createProjectSyncModel(final NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectNameVersion.getName(), projectNameVersion.getVersion());

        // TODO: Handle a boolean property not being set in detect configuration - ie need to determine if this property actually exists in the ConfigurableEnvironment - just omit this one?
        projectSyncModel.setProjectLevelAdjustments(detectProjectServiceOptions.isProjectLevelAdjustments());

        final Optional<ProjectVersionPhaseType> phase = tryGetEnumValue(ProjectVersionPhaseType.class, detectProjectServiceOptions.getProjectVersionPhase());
        phase.ifPresent(projectSyncModel::setPhase);

        final Optional<ProjectVersionDistributionType> distribution = tryGetEnumValue(ProjectVersionDistributionType.class, detectProjectServiceOptions.getProjectVersionDistribution());
        distribution.ifPresent(projectSyncModel::setDistribution);

        final Integer projectTier = detectProjectServiceOptions.getProjectTier();
        if (null != projectTier && projectTier >= 1 && projectTier <= 5) {
            projectSyncModel.setProjectTier(projectTier);
        }

        final String description = detectProjectServiceOptions.getProjectDescription();
        if (StringUtils.isNotBlank(description)) {
            projectSyncModel.setDescription(description);
        }

        final String releaseComments = detectProjectServiceOptions.getProjectVersionNotes();
        if (StringUtils.isNotBlank(releaseComments)) {
            projectSyncModel.setReleaseComments(releaseComments);
        }

        final List<ProjectCloneCategoriesType> cloneCategories = convertClonePropertyToEnum(detectProjectServiceOptions.getCloneCategories());
        if (!cloneCategories.isEmpty()) {
            projectSyncModel.setCloneCategories(cloneCategories);
        }

        final String nickname = detectProjectServiceOptions.getProjectVersionNickname();
        if (StringUtils.isNotBlank(nickname)) {
            projectSyncModel.setNickname(nickname);
        }

        final Optional<String> cloneUrl = findCloneUrl(projectNameVersion.getName()); //TODO: Be passed the clone url.
        if (cloneUrl.isPresent()) {
            logger.info("Cloning project version from release url: " + cloneUrl.get());
            projectSyncModel.setCloneFromReleaseUrl(cloneUrl.get());
        }

        return projectSyncModel;
    }

    public static <E extends Enum<E>> Optional<E> tryGetEnumValue(final Class<E> enumClass, final String value) {
        final String enumName = StringUtils.trimToEmpty(value).toUpperCase();
        try {
            return Optional.of(Enum.valueOf(enumClass, enumName));
        } catch (final IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private List<ProjectCloneCategoriesType> convertClonePropertyToEnum(final String[] cloneCategories) {
        final List<ProjectCloneCategoriesType> categories = Arrays
                                                                .stream(cloneCategories)
                                                                .filter(cloneCategoryValue -> EnumUtils.isValidEnum(ProjectCloneCategoriesType.class, cloneCategoryValue))
                                                                .map(ProjectCloneCategoriesType::valueOf)
                                                                .collect(Collectors.toList());
        logger.debug("Found clone categories:" + categories.stream().map(it -> it.toString()).collect(Collectors.joining(",")));
        return categories;
    }

    public Optional<String> findCloneUrl(String projectName) throws DetectUserFriendlyException {
        if (detectProjectServiceOptions.getCloneLatestProjectVersion()) {
            logger.info("Cloning the most recent project version.");
            return findLatestProjectVersionCloneUrl(blackDuckServicesFactory.createBlackDuckService(), blackDuckServicesFactory.createProjectService(), projectName);
        } else if (StringUtils.isNotBlank(detectProjectServiceOptions.getCloneVersionName())) {
            return findNamedCloneUrl(projectName, detectProjectServiceOptions.getCloneVersionName(), blackDuckServicesFactory.createProjectService());
        } else {
            logger.debug("No clone project or version name supplied. Will not clone.");
            return Optional.empty();
        }
    }

    public Optional<String> findNamedCloneUrl(final String cloneProjectName, String cloneProjectVersionName, final ProjectService projectService) throws DetectUserFriendlyException {
        try {
            final Optional<ProjectVersionWrapper> projectVersionWrapper = projectService.getProjectVersion(cloneProjectName, cloneProjectVersionName);
            if (projectVersionWrapper.isPresent()) {
                return projectVersionWrapper.get().getProjectVersionView().getHref();
            } else {
                logger.warn(String.format("Project/version %s/%s not found for cloning", cloneProjectName, cloneProjectVersionName));
                return Optional.empty();
            }
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("Error finding project/version (%s/%s) to clone, or getting its release url.", cloneProjectName, cloneProjectVersionName), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public Optional<String> findLatestProjectVersionCloneUrl(BlackDuckService blackDuckService, ProjectService projectService, String projectName) throws DetectUserFriendlyException {
        try {
            Optional<ProjectView> projectView = projectService.getProjectByName(projectName);
            if (projectView.isPresent()) {
                List<ProjectVersionView> projectVersionViews = blackDuckService.getAllResponses(projectView.get(), ProjectView.VERSIONS_LINK_RESPONSE);
                if (projectVersionViews.size() == 0) {
                    logger.warn("Could not find an existing project version to clone from. Ensure the project exists when using the latest clone flag.");
                    return Optional.empty();
                } else {
                    return projectVersionViews.stream()
                               .max(Comparator.comparing(ProjectVersionView::getCreatedAt))
                               .flatMap(BlackDuckView::getHref);
                }
            } else {
                logger.warn("Could not find existing project to clone from. Ensure the project exists when using the latest clone flag.");
                return Optional.empty();
            }
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("Error finding latest version to clone, or getting its release url."), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public void setApplicationId(final ProjectView projectView, final String applicationId) throws DetectUserFriendlyException {
        if (StringUtils.isBlank(applicationId)) {
            logger.info("No Application ID to set");
            return;
        }

        try {
            logger.info("Populating project Application ID");
            projectMappingService.populateApplicationId(projectView, applicationId);
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("Unable to set Application ID for project: %s", projectView.getName()), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}
