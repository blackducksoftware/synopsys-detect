/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.analytics;

import java.io.IOException;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class AnalyticsConfigurationService {
    private static final BlackDuckPath INTEGRATION_SETTINGS_PATH = new BlackDuckPath("/api/internal/integration-settings");
    private static final String MIME_TYPE = "application/vnd.blackducksoftware.integration-setting-1+json";

    private final Gson gson;

    public AnalyticsConfigurationService(Gson gson) {
        this.gson = gson;
    }

    public AnalyticsSetting fetchAnalyticsSetting(BlackDuckApiClient blackDuckService) throws IntegrationException, IOException {
        HttpUrl url = blackDuckService.getUrl(INTEGRATION_SETTINGS_PATH).appendRelativeUrl("/analytics");

        Request request = new Request.Builder()
                              .url(url)
                              .method(HttpMethod.GET)
                              .acceptMimeType(MIME_TYPE)
                              .build();
        try (Response response = blackDuckService.execute(request)) {
            response.throwExceptionForError();
            return gson.fromJson(response.getContentString(), AnalyticsSetting.class);
        }
    }
}
