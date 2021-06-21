/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.http.BlackDuckRequestFilter;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;

public class MapToParentOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BlackDuckApiClient blackDuckService;
    private ProjectService projectService;
    private ProjectBomService projectBomService;

    public MapToParentOperation(BlackDuckApiClient blackDuckService, ProjectService projectService, ProjectBomService projectBomService) {
        this.blackDuckService = blackDuckService;
        this.projectService = projectService;
        this.projectBomService = projectBomService;
    }

    public void mapToParentProjectVersion(String parentProjectName, String parentVersionName, ProjectVersionWrapper projectVersionWrapper)
        throws DetectUserFriendlyException {
        logger.debug("Will attempt to add this project to a parent.");
        String projectName = projectVersionWrapper.getProjectView().getName();
        String projectVersionName = projectVersionWrapper.getProjectVersionView().getVersionName();
        if (StringUtils.isBlank(parentProjectName) || StringUtils.isBlank(parentVersionName)) {
            String errorReason = "Both the parent project name and the parent project version name must be specified if either is specified.";
            throw new DetectUserFriendlyException(errorReason, ExitCodeType.FAILURE_CONFIGURATION);
        }
        try {
            Optional<ProjectVersionWrapper> parentWrapper = projectService.getProjectVersion(parentProjectName, parentVersionName);
            if (parentWrapper.isPresent()) {
                ProjectVersionView parentProjectVersionView = parentWrapper.get().getProjectVersionView();

                BlackDuckRequestFilter filter = BlackDuckRequestFilter.createFilterWithSingleValue("bomComponentSource", "custom_project");
                BlackDuckMultipleRequest<ProjectVersionComponentView> spec = new BlackDuckRequestBuilder()
                                                                                 .commonGet()
                                                                                 .addBlackDuckFilter(filter)
                                                                                 .buildBlackDuckRequest(parentProjectVersionView.metaComponentsLink());

                List<ProjectVersionComponentView> components = blackDuckService.getAllResponses(spec);
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
            String errorReason = "Unable to add project to parent.";
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR);
        }

    }
}
