package com.synopsys.integration.detect.workflow.blackduck.analytics;

import java.io.IOException;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;

public class AnalyticsConfigurationService {
    private static final BlackDuckPath INTEGRATION_SETTINGS_PATH = new BlackDuckPath("/api/internal/integration-settings");

    private final BlackDuckService blackDuckService;
    private final Gson gson;

    public AnalyticsConfigurationService(BlackDuckService blackDuckService, Gson gson) {
        this.blackDuckService = blackDuckService;
        this.gson = gson;
    }

    public AnalyticsSetting fetchAnalyticsSetting() throws IntegrationException, IOException {
        String uri = blackDuckService.getUri(INTEGRATION_SETTINGS_PATH) + "/analytics";

        try (Response response = blackDuckService.get(uri)) {
            response.throwExceptionForError();
            return gson.fromJson(response.getContentString(), AnalyticsSetting.class);
        }
    }
}
