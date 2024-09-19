package com.blackduck.integration.detect.workflow.blackduck.analytics;

import java.io.IOException;

import com.blackduck.integration.blackduck.api.core.BlackDuckPath;
import com.blackduck.integration.blackduck.api.core.response.UrlSingleResponse;
import com.blackduck.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.blackduck.integration.blackduck.http.BlackDuckRequestBuilder;
import com.blackduck.integration.blackduck.service.BlackDuckApiClient;
import com.blackduck.integration.blackduck.service.request.BlackDuckSingleRequest;
import com.blackduck.integration.exception.IntegrationException;

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
