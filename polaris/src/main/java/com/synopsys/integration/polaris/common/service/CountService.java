/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import java.util.Objects;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.query.model.CountV0;
import com.synopsys.integration.polaris.common.api.query.model.CountV0Attributes;
import com.synopsys.integration.polaris.common.api.query.model.CountV0Resources;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;

public class CountService {
    private final PolarisService polarisService;

    public CountService(final PolarisService polarisService) {
        this.polarisService = polarisService;
    }

    public CountV0Resources getCountV0ResourcesFromIssueApiUrl(final String issueApiUrl) throws IntegrationException {
        final Request.Builder requestBuilder = PolarisRequestFactory.createDefaultBuilder()
                                                   .url(new HttpUrl(issueApiUrl));
        final Request request = requestBuilder.build();
        return polarisService.get(CountV0Resources.class, request);
    }

    public Integer getTotalIssueCountFromIssueApiUrl(final String issueApiUrl) throws IntegrationException {
        return getCountV0ResourcesFromIssueApiUrl(issueApiUrl).getData().stream()
                   .map(CountV0::getAttributes)
                   .map(CountV0Attributes::getValue)
                   .filter(Objects::nonNull)
                   .reduce(0, Integer::sum);
    }
}
