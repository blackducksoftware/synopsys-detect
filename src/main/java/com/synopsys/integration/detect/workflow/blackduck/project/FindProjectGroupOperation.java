package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.response.UrlMultipleResponses;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.view.ProjectGroupsView;
import com.synopsys.integration.blackduck.http.BlackDuckQuery;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.http.BlackDuckRequestFilter;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class FindProjectGroupOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackDuckApiClient blackDuckApiClient;
    private final UrlMultipleResponses<ProjectGroupsView> projectGroupsResponses;

    public FindProjectGroupOperation(BlackDuckApiClient blackDuckApiClient, ApiDiscovery apiDiscovery) {
        this.blackDuckApiClient = blackDuckApiClient;
        projectGroupsResponses = apiDiscovery.metaMultipleResponses(ApiDiscovery.PROJECT_GROUPS_PATH);
    }

    public HttpUrl findProjectGroup(String projectGroupName) throws IntegrationException, DetectUserFriendlyException {
        BlackDuckRequestBuilder blackDuckRequestBuilder = new BlackDuckRequestBuilder()
            .commonGet()
            .addBlackDuckQuery(new BlackDuckQuery("name", projectGroupName))
            .addBlackDuckFilter(BlackDuckRequestFilter.createFilterWithSingleValue("exactName", "true"));

        BlackDuckMultipleRequest<ProjectGroupsView> requestMultiple = blackDuckRequestBuilder.buildBlackDuckRequest(projectGroupsResponses);
        List<ProjectGroupsView> response = blackDuckApiClient.getAllResponses(requestMultiple);
        if (response.size() != 1) {
            throw new DetectUserFriendlyException(
                "Project Group Name must have exactly 1 match on Black Duck, instead '" + projectGroupName + "' had " + response.size() + " matches.",
                ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR
            );
        }
        ProjectGroupsView result = response.get(0);
        return result.getHref();
    }
}
