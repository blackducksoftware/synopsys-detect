package com.synopsys.integration.detect.workflow.blackduck.analytics;

import java.io.IOException;

import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.api.core.response.UrlSingleResponse;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckSingleRequest;
import com.synopsys.integration.exception.IntegrationException;

public class AnalyticsConfigurationService {
    private static final BlackDuckPath<AnalyticsSetting> INTEGRATION_SETTINGS_PATH = new BlackDuckPath<>(
        "/api/internal/integration-settings/analytics",
        AnalyticsSetting.class,
        false
    );
    private static final String MIME_TYPE = "application/vnd.blackducksoftware.integration-setting-1+json";

    public AnalyticsSetting fetchAnalyticsSetting(ApiDiscovery apiDiscovery, BlackDuckApiClient blackDuckApiClient) throws IntegrationException, IOException {
        UrlSingleResponse<AnalyticsSetting> urlResponse = apiDiscovery.metaSingleResponse(INTEGRATION_SETTINGS_PATH);

        BlackDuckSingleRequest<AnalyticsSetting> spec = new BlackDuckRequestBuilder()
            .commonGet()
            .acceptMimeType(MIME_TYPE)
            .buildBlackDuckRequest(urlResponse);

        return blackDuckApiClient.getResponse(spec);
    }

}
