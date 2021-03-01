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
import com.synopsys.integration.polaris.common.api.auth.model.Context;
import com.synopsys.integration.polaris.common.api.auth.model.ContextAttributes;
import com.synopsys.integration.polaris.common.api.auth.model.ContextResources;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.request.Request;

public class ContextsService {
    private static final TypeToken CONTEXT_RESOURCES = new TypeToken<ContextResources>() {};

    private final PolarisService polarisService;
    private final AccessTokenPolarisHttpClient polarisHttpClient;

    public ContextsService(final PolarisService polarisService, final AccessTokenPolarisHttpClient polarisHttpClient) {
        this.polarisService = polarisService;
        this.polarisHttpClient = polarisHttpClient;
    }

    public List<Context> getAllContexts() throws IntegrationException {
        Request request = PolarisRequestFactory.createDefaultPolarisGetRequest(polarisHttpClient.getPolarisServerUrl() + AuthService.CONTEXTS_API_SPEC.getSpec());
        return polarisService.getAllResponses(request, CONTEXT_RESOURCES.getType());
    }

    public Optional<Context> getCurrentContext() throws IntegrationException {
        return getAllContexts().stream()
                   .filter(this::isCurrentContext)
                   .findFirst();
    }

    private Boolean isCurrentContext(final Context context) {
        return Optional.ofNullable(context)
                   .map(Context::getAttributes)
                   .map(ContextAttributes::getCurrent)
                   .orElse(Boolean.FALSE);
    }

}
