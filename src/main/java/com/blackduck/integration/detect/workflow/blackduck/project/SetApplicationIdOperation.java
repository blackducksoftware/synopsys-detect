package com.blackduck.integration.detect.workflow.blackduck.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.api.manual.view.ProjectView;
import com.blackduck.integration.blackduck.service.dataservice.ProjectMappingService;
import com.blackduck.integration.detect.configuration.DetectUserFriendlyException;
import com.blackduck.integration.detect.configuration.enumeration.ExitCodeType;
import com.blackduck.integration.exception.IntegrationException;

public class SetApplicationIdOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectMappingService projectMappingService;

    public SetApplicationIdOperation(ProjectMappingService projectMappingService) {
        this.projectMappingService = projectMappingService;
    }

    public void setApplicationId(ProjectView projectView, String applicationId) throws DetectUserFriendlyException {
        try {
            logger.debug("Populating project 'Application ID'");
            projectMappingService.populateApplicationId(projectView, applicationId);
        } catch (IntegrationException e) {
            String errorReason = String.format("Unable to set Application ID for project: %s", projectView.getName());
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}
