/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.PolarisResource;
import com.synopsys.integration.polaris.common.api.PolarisResources;
import com.synopsys.integration.polaris.common.api.PolarisResourcesSingle;
import com.synopsys.integration.polaris.common.api.auth.PolarisRelationshipLinks;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestCreator;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestWrapper;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.request.PolarisRequestSpec;
import com.synopsys.integration.polaris.common.request.param.PolarisParamBuilder;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;

public class AuthService {
    public static final String AUTH_API_SPEC_STRING = "/api/auth";
    public static final PolarisRequestSpec ROLE_ASSIGNMENTS_API_SPEC = PolarisRequestSpec.of(AUTH_API_SPEC_STRING + "/role-assignments");
    public static final PolarisRequestSpec CONTEXTS_API_SPEC = PolarisRequestSpec.of(AUTH_API_SPEC_STRING + "/contexts");
    public static final PolarisRequestSpec USERS_API_SPEC = PolarisRequestSpec.of(AUTH_API_SPEC_STRING + "/users");
    public static final PolarisRequestSpec GROUPS_API_SPEC = PolarisRequestSpec.of(AUTH_API_SPEC_STRING + "/groups");
    private final AccessTokenPolarisHttpClient polarisHttpClient;
    private final PolarisService polarisService;

    public AuthService(final AccessTokenPolarisHttpClient polarisHttpClient, final PolarisService polarisService) {
        this.polarisHttpClient = polarisHttpClient;
        this.polarisService = polarisService;
    }

    public <R extends PolarisResource, S extends PolarisResources<R>> List<R> getAll(final PolarisRequestSpec polarisRequestSpec, final Class<S> resourcesType) throws IntegrationException {
        return getFiltered(polarisRequestSpec, (PolarisParamBuilder) null, resourcesType);
    }

    public <R extends PolarisResource, S extends PolarisResources<R>> List<R> getFiltered(final PolarisRequestSpec polarisRequestSpec, final PolarisParamBuilder paramBuilder, final Class<S> resourcesType) throws IntegrationException {
        final List<PolarisParamBuilder> paramBuilders = paramBuilder == null ?
                Collections.emptyList() :
                Collections.singletonList(paramBuilder);
        return getFiltered(polarisRequestSpec, paramBuilders, resourcesType);
    }

    public <R extends PolarisResource, S extends PolarisResources<R>> List<R> getFiltered(final PolarisRequestSpec polarisRequestSpec, final Collection<PolarisParamBuilder> paramBuilders, final Class<S> resourcesType)
        throws IntegrationException {
        final HttpUrl url = polarisHttpClient.appendToPolarisUrl(polarisRequestSpec.getSpec());
        final PolarisPagedRequestCreator createPagedRequest = (limit, offset) -> createPagedRequest(url, paramBuilders, limit, offset);
        final PolarisPagedRequestWrapper pagedRequestWrapper = new PolarisPagedRequestWrapper(createPagedRequest, resourcesType);
        return polarisService.getAllResponses(pagedRequestWrapper);
    }

    public <R extends PolarisResource, S extends PolarisResourcesSingle<R>, T> Optional<T> getAttributeFromRelationship(final PolarisRelationshipLinks relationshipLinks, final Function<R, T> extractAttribute, final Class<S> resourcesType)
        throws IntegrationException {
        final String uri = relationshipLinks.getRelated();
        final Request resourceRequest = PolarisRequestFactory.createDefaultPolarisPagedGetRequest(uri);
        final PolarisResourcesSingle<R> response = polarisService.get(resourcesType, resourceRequest);

        return response.getData().map(extractAttribute);
    }

    public PolarisPagedRequestCreator generatePagedRequestCreatorWithInclude(final PolarisRequestSpec polarisRequestSpec, final String... included) throws IntegrationException {
        return generatePagedRequestCreatorWithInclude(polarisRequestSpec, Collections.emptyList(), included);
    }

    public PolarisPagedRequestCreator generatePagedRequestCreatorWithInclude(final PolarisRequestSpec polarisRequestSpec, final PolarisParamBuilder paramBuilder, final String... included) throws IntegrationException {
        return generatePagedRequestCreatorWithInclude(polarisRequestSpec, Collections.singletonList(paramBuilder), included);
    }

    public PolarisPagedRequestCreator generatePagedRequestCreatorWithInclude(final PolarisRequestSpec polarisRequestSpec, final Collection<PolarisParamBuilder> paramBuilders, final String... included) throws IntegrationException {
        final Set<PolarisParamBuilder> allParamBuilders = new HashSet<>(paramBuilders);
        final Map<String, Set<String>> queryParameters = new HashMap<>(allParamBuilders.size());
        for (final String include : included) {
            final PolarisParamBuilder includeFilter = PolarisParamBuilder.createIncludeFilter(polarisRequestSpec.getType(), include);
            allParamBuilders.add(includeFilter);
        }
        for (final PolarisParamBuilder paramBuilder : allParamBuilders) {
            final Map.Entry<String, String> queryParam = paramBuilder.build();
            queryParameters.computeIfAbsent(queryParam.getKey(), k -> new HashSet<>()).add(queryParam.getValue());
        }

        final HttpUrl url = polarisHttpClient.appendToPolarisUrl(polarisRequestSpec.getSpec());
        return (limit, offset) -> PolarisRequestFactory.createDefaultPagedRequestBuilder(limit, offset).url(url).queryParameters(queryParameters).build();
    }

    private Request createPagedRequest(final HttpUrl url, final Collection<PolarisParamBuilder> paramBuilders, final int limit, final int offset) {
        final Request.Builder pagedRequestBuilder = PolarisRequestFactory.createDefaultPagedRequestBuilder(limit, offset);
        pagedRequestBuilder.url(url);
        if (paramBuilders != null) {
            addParams(pagedRequestBuilder, paramBuilders);
        }

        return pagedRequestBuilder.build();
    }

    private void addParams(final Request.Builder requestBuilder, final Collection<PolarisParamBuilder> paramBuilders) {
        for (final PolarisParamBuilder paramBuilder : paramBuilders) {
            if (paramBuilder != null) {
                final Map.Entry<String, String> params = paramBuilder.build();
                requestBuilder.addQueryParameter(params.getKey(), params.getValue());
            }
        }
    }

}
