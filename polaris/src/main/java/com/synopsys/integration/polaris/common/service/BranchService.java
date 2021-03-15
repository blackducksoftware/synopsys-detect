/**
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
import com.synopsys.integration.polaris.common.api.common.model.branch.BranchV0Resource;
import com.synopsys.integration.polaris.common.api.common.model.branch.BranchV0Resources;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestCreator;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestWrapper;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.request.param.FilterConstants;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;

public class BranchService {
    private static final TypeToken BRANCH_RESOURCES = new TypeToken<BranchV0Resources>() {
    };

    private final AccessTokenPolarisHttpClient polarisHttpClient;
    private final PolarisService polarisService;

    public BranchService(final AccessTokenPolarisHttpClient polarisHttpClient, final PolarisService polarisService) {
        this.polarisHttpClient = polarisHttpClient;
        this.polarisService = polarisService;
    }

    public List<BranchV0Resource> getBranchesForProject(final String projectId) throws IntegrationException {
        final PolarisPagedRequestCreator createPagedRequest = (limit, offset) -> createBranchesGetRequest(limit, offset, projectId);
        final PolarisPagedRequestWrapper pagedRequestWrapper = new PolarisPagedRequestWrapper(createPagedRequest, BRANCH_RESOURCES.getType());
        return polarisService.getAllResponses(pagedRequestWrapper);
    }

    public Optional<BranchV0Resource> getBranchForProjectByName(final String projectId, final String branchName) throws IntegrationException {
        final Request.Builder requestBuilder = createBranchForProjectIdRequestBuilder(projectId);
        requestBuilder.addQueryParameter(FilterConstants.FILTER_BRANCH_NAME_CONTAINS, branchName);
        final Request request = requestBuilder.build();

        return polarisService.getFirstResponse(request, BRANCH_RESOURCES.getType());
    }

    public Request createBranchesGetRequest(final int limit, final int offset, final String projectId) {
        final Request.Builder requestBuilder = createBranchForProjectIdRequestBuilder(projectId);
        PolarisRequestFactory.populatePagedRequestBuilder(requestBuilder, limit, offset);
        return requestBuilder.build();
    }

    private Request.Builder createBranchForProjectIdRequestBuilder(final String projectId) {
        final HttpUrl url = polarisHttpClient.appendToPolarisUrl(PolarisService.BRANCHES_API_SPEC);
        return PolarisRequestFactory.createDefaultRequestBuilder()
                   .addQueryParameter(FilterConstants.FILTER_BRANCH_PROJECT_ID_EQUALS, projectId)
                   .url(url);
    }

}
