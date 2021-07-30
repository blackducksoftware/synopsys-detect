/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import com.synopsys.integration.blackduck.api.core.response.UrlMultipleResponses;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.rest.HttpUrl;

public class DetectRapidScanRequestBuilder {
    private final BlackDuckRequestBuilder blackDuckRequestBuilder;

    public DetectRapidScanRequestBuilder() {
        this.blackDuckRequestBuilder = new BlackDuckRequestBuilder()
                                           .commonGet()
                                           .acceptMimeType(DeveloperScanComponentResultView.CURRENT_MEDIA_TYPE);
    }

    public BlackDuckResponseRequest createResponseRequest(HttpUrl httpUrl) {
        return blackDuckRequestBuilder.buildBlackDuckResponseRequest(httpUrl);
    }

    public BlackDuckMultipleRequest<DeveloperScanComponentResultView> createRequest(HttpUrl httpUrl) {
        return blackDuckRequestBuilder.buildBlackDuckRequest(new UrlMultipleResponses<>(httpUrl, DeveloperScanComponentResultView.class));
    }

}