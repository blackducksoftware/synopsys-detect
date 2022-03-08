package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.blackduck.project.options.CloneFindResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class FindCloneByLatestOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectService projectService;
    private final BlackDuckApiClient blackDuckService;

    public FindCloneByLatestOperation(ProjectService projectService, BlackDuckApiClient blackDuckService) {
        this.projectService = projectService;
        this.blackDuckService = blackDuckService;
    }

    public CloneFindResult findLatestProjectVersionCloneUrl(String projectName) throws DetectUserFriendlyException {
        try {
            Optional<ProjectView> projectView = projectService.getProjectByName(projectName);
            if (projectView.isPresent()) {
                List<ProjectVersionView> projectVersionViews = blackDuckService.getAllResponses(projectView.get().metaVersionsLink());
                if (projectVersionViews.isEmpty()) {
                    logger.warn("Could not find an existing project version to clone from. Ensure the project exists when using the latest clone flag.");
                    return CloneFindResult.empty();
                } else {
                    Optional<HttpUrl> url = projectVersionViews.stream()
                        .max(Comparator.comparing(ProjectVersionView::getCreatedAt))
                        .map(BlackDuckView::getHref);
                    return new CloneFindResult(url.orElse(null));
                }
            } else {
                logger.warn("Could not find existing project to clone from. Ensure the project exists when using the latest clone flag.");
                return CloneFindResult.empty();
            }
        } catch (IntegrationException e) {
            String errorReason = "Error finding latest version to clone, or getting its release url.";
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}
