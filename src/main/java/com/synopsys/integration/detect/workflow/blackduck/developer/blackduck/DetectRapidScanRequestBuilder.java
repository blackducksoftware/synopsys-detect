package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import com.synopsys.integration.blackduck.api.core.response.UrlMultipleResponses;
import com.synopsys.integration.blackduck.api.generated.view.ScanFullResultView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.rest.HttpUrl;

public class DetectRapidScanRequestBuilder {
    // change to v6 for full-result usage.  v5 currently appears to return malformed json.
    public static final String CURRENT_MEDIA_TYPE = "application/vnd.blackducksoftware.scan-6+json";
    private final BlackDuckRequestBuilder blackDuckRequestBuilder;

    public DetectRapidScanRequestBuilder() {
        this.blackDuckRequestBuilder = new BlackDuckRequestBuilder()
            .commonGet()
            .acceptMimeType(CURRENT_MEDIA_TYPE);
    }

    public BlackDuckResponseRequest createResponseRequest(HttpUrl httpUrl) {
        return blackDuckRequestBuilder.buildBlackDuckResponseRequest(httpUrl);
    }

    public BlackDuckMultipleRequest<DeveloperScansScanView> createRequest(HttpUrl httpUrl) {
        return blackDuckRequestBuilder.buildBlackDuckRequest(new UrlMultipleResponses<>(httpUrl, DeveloperScansScanView.class));
    }
    public BlackDuckMultipleRequest<ScanFullResultView> createFullRequest(HttpUrl httpUrl) {
        return blackDuckRequestBuilder.buildBlackDuckRequest(new UrlMultipleResponses<>(httpUrl, ScanFullResultView.class));
    }

}