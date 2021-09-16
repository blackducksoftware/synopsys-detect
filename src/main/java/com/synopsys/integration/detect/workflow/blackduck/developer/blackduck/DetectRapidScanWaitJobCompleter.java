/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.wait.WaitJobCompleter;

public class DetectRapidScanWaitJobCompleter implements WaitJobCompleter<List<DeveloperScanComponentResultView>> {
    private final BlackDuckApiClient blackDuckApiClient;
    private final List<HttpUrl> resultUrl;

    public DetectRapidScanWaitJobCompleter(BlackDuckApiClient blackDuckApiClient, List<HttpUrl> resultUrl) {
        this.blackDuckApiClient = blackDuckApiClient;
        this.resultUrl = resultUrl;
    }

    @Override
    public List<DeveloperScanComponentResultView> complete() throws IntegrationException {
        List<DeveloperScanComponentResultView> allComponents = new ArrayList<>();
        for (HttpUrl url : resultUrl) {
            allComponents.addAll(getScanResultsForUrl(url));
        }
        return allComponents;
    }

    private List<DeveloperScanComponentResultView> getScanResultsForUrl(HttpUrl url) throws IntegrationException {
        BlackDuckMultipleRequest<DeveloperScanComponentResultView> request =
            new DetectRapidScanRequestBuilder()
                .createRequest(url);
        return blackDuckApiClient.getAllResponses(request);
    }

    @Override
    public List<DeveloperScanComponentResultView> handleTimeout() throws IntegrationTimeoutException {
        throw new IntegrationTimeoutException("Error getting developer scan result. Timeout may have occurred.");
    }

}
