/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.workflow.hub;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.generated.view.UserGroupView;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectMappingService;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ProjectUsersService;
import com.synopsys.integration.blackduck.service.UserGroupService;
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

    public ProjectVersionWrapper createOrUpdateHubProject(final NameVersion projectNameVersion, final String applicationId) throws IntegrationException, DetectUserFriendlyException {
        final ProjectService projectService = blackDuckServicesFactory.createProjectService();
        final BlackDuckService hubService = blackDuckServicesFactory.createBlackDuckService();
        final ProjectSyncModel projectSyncModel = createProjectSyncModel(projectNameVersion, projectService, hubService);
        final boolean forceUpdate = detectProjectServiceOptions.isForceProjectVersionUpdate();
        final ProjectVersionWrapper projectVersionWrapper = projectService.syncProjectAndVersion(projectSyncModel, forceUpdate);
        setApplicationId(projectVersionWrapper.getProjectView(), applicationId);



        /////////// TODO Experimental //////////////
        final String givenGroupName = "testGroup1";

        final UserGroupService userGroupService = blackDuckServicesFactory.createUserGroupService();
        final Optional<UserGroupView> givenGroup;
        try {
            givenGroup = userGroupService.getGroupByName(givenGroupName);
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("Error finding given group (%s) to add to project.", givenGroupName), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
        if (givenGroup.isPresent()) {
            logger.info(String.format("*** Found Group: %s", givenGroup.get().getName()));
            final ProjectUsersService projectUsersService = blackDuckServicesFactory.createProjectUsersService();
//            projectUsersService.getAssignedGroupsToProject()
        } else {
            logger.info("*** Did NOT find Group: testGroup1");
        }

        ////////////////////////////////////////////
        return projectVersionWrapper;
    }

    public ProjectSyncModel createProjectSyncModel(final NameVersion projectNameVersion, final ProjectService projectService, final BlackDuckService hubService) throws DetectUserFriendlyException {
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

        final Optional<String> cloneUrl = findCloneUrl(projectNameVersion, projectService, hubService);
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

    public Optional<String> findCloneUrl(final NameVersion projectNameVersion, final ProjectService projectService, final BlackDuckService hubService) throws DetectUserFriendlyException {
        final String cloneProjectName = projectNameVersion.getName();
        final String cloneProjectVersionName = detectProjectServiceOptions.getCloneVersionName();
        if (StringUtils.isBlank(cloneProjectName) || StringUtils.isBlank(cloneProjectVersionName)) {
            logger.debug("No clone project or version name supplied. Will not clone.");
            return Optional.empty();
        }
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
