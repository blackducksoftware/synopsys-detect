package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.blackduck.project.options.CloneFindResult;
import com.synopsys.integration.exception.IntegrationException;

public class FindCloneByNameOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectService projectService;

    public FindCloneByNameOperation(ProjectService projectService) {
        this.projectService = projectService;
    }

    public CloneFindResult findNamedCloneUrl(String cloneProjectName, String cloneProjectVersionName) throws DetectUserFriendlyException {
        try {
            Optional<ProjectVersionWrapper> projectVersionWrapper = projectService.getProjectVersion(cloneProjectName, cloneProjectVersionName);
            if (projectVersionWrapper.isPresent()) {
                return CloneFindResult.of(projectVersionWrapper.get().getProjectVersionView().getHref());
            } else {
                logger.warn(String.format("Project/version %s/%s not found for cloning", cloneProjectName, cloneProjectVersionName));
                return CloneFindResult.empty();
            }
        } catch (IntegrationException e) {
            String errorReason = String.format("Error finding project/version (%s/%s) to clone, or getting its release url.", cloneProjectName, cloneProjectVersionName);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

}
