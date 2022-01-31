package com.synopsys.integration.detect.workflow.blackduck.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.dataservice.ProjectMappingService;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;

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
