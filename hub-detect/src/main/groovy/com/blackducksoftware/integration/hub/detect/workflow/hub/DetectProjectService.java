/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.synopsys.integration.blackduck.api.core.ProjectRequestBuilder;
import com.synopsys.integration.blackduck.api.generated.component.ProjectRequest;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class DetectProjectService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final DetectProjectServiceOptions detectProjectServiceOptions;

    public DetectProjectService(final BlackDuckServicesFactory blackDuckServicesFactory, final DetectProjectServiceOptions detectProjectServiceOptions) {
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.detectProjectServiceOptions = detectProjectServiceOptions;
    }

    public ProjectVersionWrapper createOrUpdateHubProject(NameVersion projectNameVersion) throws IntegrationException, DetectUserFriendlyException, InterruptedException {
        final ProjectService projectService = blackDuckServicesFactory.createProjectService();
        final BlackDuckService hubService = blackDuckServicesFactory.createBlackDuckService();
        final ProjectRequest projectRequest = createProjectRequest(projectNameVersion, projectService, hubService);
        final boolean forceUpdate = detectProjectServiceOptions.isForceProjectVersionUpdate();
        return projectService.syncProjectAndVersion(projectRequest, forceUpdate);
    }

    public ProjectRequest createProjectRequest(final NameVersion projectNameVersion, final ProjectService projectService, final BlackDuckService hubService) throws DetectUserFriendlyException {
        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();

        projectRequestBuilder.setProjectName(projectNameVersion.getName());
        projectRequestBuilder.setVersionName(projectNameVersion.getVersion());

        projectRequestBuilder.setProjectLevelAdjustments(detectProjectServiceOptions.isProjectLevelAdjustments());
        projectRequestBuilder.setPhase(detectProjectServiceOptions.getProjectVersionPhase());
        projectRequestBuilder.setDistribution(detectProjectServiceOptions.getProjectVersionDistribution());
        projectRequestBuilder.setProjectTier(detectProjectServiceOptions.getProjectTier());
        projectRequestBuilder.setDescription(detectProjectServiceOptions.getProjectDescription());
        projectRequestBuilder.setReleaseComments(detectProjectServiceOptions.getProjectVersionNotes());
        projectRequestBuilder.setCloneCategories(convertClonePropertyToEnum(detectProjectServiceOptions.getCloneCategories()));

        final Optional<String> cloneUrl = findCloneUrl(projectNameVersion, projectService, hubService);
        if (cloneUrl.isPresent()) {
            logger.info("Cloning project version from release url: " + cloneUrl.get());
            projectRequestBuilder.setCloneFromReleaseUrl(cloneUrl.get());
        }

        return projectRequestBuilder.build();
    }

    private List<ProjectCloneCategoriesType> convertClonePropertyToEnum(final String[] cloneCategories) {
        final List<ProjectCloneCategoriesType> categories = new ArrayList<>();
        for (final String category : cloneCategories) {
            categories.add(ProjectCloneCategoriesType.valueOf(category));
        }
        logger.debug("Found clone categories:" + categories.stream().map(it -> it.toString()).collect(Collectors.joining(",")));
        return categories;
    }

    public Optional<String> findCloneUrl(NameVersion projectNameVersion, final ProjectService projectService, final BlackDuckService hubService) throws DetectUserFriendlyException {
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
                return Optional.empty();
            }
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException("Unable to find clone release url for supplied clone version name.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

    }
}
