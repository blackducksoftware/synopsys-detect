/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
