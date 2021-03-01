/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
