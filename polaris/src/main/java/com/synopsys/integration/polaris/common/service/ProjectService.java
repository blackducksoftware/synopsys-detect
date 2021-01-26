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

import java.util.List;
import java.util.Optional;

import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resource;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resources;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestWrapper;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.request.param.FilterConstants;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;

public class ProjectService {
    private static final TypeToken PROJECT_RESOURCES = new TypeToken<ProjectV0Resources>() {};

    private final AccessTokenPolarisHttpClient polarisHttpClient;
    private final PolarisService polarisService;

    public ProjectService(final AccessTokenPolarisHttpClient polarisHttpClient, final PolarisService polarisService) {
        this.polarisHttpClient = polarisHttpClient;
        this.polarisService = polarisService;
    }

    public Optional<ProjectV0Resource> getProjectByName(final String projectName) throws IntegrationException {
        final HttpUrl url = polarisHttpClient.appendToPolarisUrl(PolarisService.PROJECT_API_SPEC);
        final Request request =
            PolarisRequestFactory.createDefaultRequestBuilder()
                .addQueryParameter(FilterConstants.FILTER_PROJECT_NAME_CONTAINS, projectName)
                .url(url)
                .build();
        return polarisService.getFirstResponse(request, PROJECT_RESOURCES.getType());
    }

    public List<ProjectV0Resource> getAllProjects() throws IntegrationException {
        final PolarisPagedRequestWrapper pagedRequestWrapper = new PolarisPagedRequestWrapper(this::createProjectGetRequest, PROJECT_RESOURCES.getType());
        return polarisService.getAllResponses(pagedRequestWrapper);
    }

    public Request createProjectGetRequest(final int limit, final int offset) {
        final HttpUrl url = polarisHttpClient.appendToPolarisUrl(PolarisService.PROJECT_API_SPEC);
        return PolarisRequestFactory.createDefaultPagedRequestBuilder(limit, offset)
                   .url(url)
                   .build();
    }

}
