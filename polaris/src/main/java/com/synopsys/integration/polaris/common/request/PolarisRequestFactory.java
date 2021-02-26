/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;

public class PolarisRequestFactory {
    public static final String DEFAULT_MIME_TYPE = "application/vnd.api+json";

    public static final String LIMIT_PARAMETER = "page[limit]";
    public static final String OFFSET_PARAMETER = "page[offset]";

    public static final int DEFAULT_LIMIT = 25;
    public static final int DEFAULT_OFFSET = 0;

    public static Request createDefaultPolarisGetRequest(final String requestUri) throws IntegrationException {
        HttpUrl httpUrl = new HttpUrl(requestUri);
        return createDefaultBuilder()
                   .url(httpUrl)
                   .build();
    }

    public static Request createDefaultPolarisPagedGetRequest(final String requestUri) throws IntegrationException {
        return createCommonPolarisPagedGetRequest(requestUri, DEFAULT_LIMIT);
    }

    public static Request createCommonPolarisPagedGetRequest(final String requestUri, final int limit) throws IntegrationException {
        return createCommonPolarisPagedGetRequest(requestUri, limit, DEFAULT_OFFSET);
    }

    public static Request createCommonPolarisPagedGetRequest(final String requestUri, final int limit, final int offset) throws IntegrationException {
        HttpUrl httpUrl = new HttpUrl(requestUri);
        return createDefaultPagedRequestBuilder(limit, offset)
                   .url(httpUrl)
                   .build();
    }

    public static Request.Builder createDefaultRequestBuilder() {
        return populatePagedRequestBuilder(createDefaultBuilder(), DEFAULT_LIMIT, DEFAULT_OFFSET);
    }

    public static Request.Builder createDefaultPagedRequestBuilder(final int limit, final int offset) {
        return populatePagedRequestBuilder(createDefaultBuilder(), limit, offset);
    }

    public static Request.Builder populatePagedRequestBuilder(final Request.Builder requestBuilder, final int limit, final int offset) {
        Map<String, Set<String>> queryParameters = requestBuilder.getQueryParameters();
        if (null == queryParameters) {
            requestBuilder.queryParameters(new HashMap<>());
            queryParameters = requestBuilder.getQueryParameters();
        }
        queryParameters.put(LIMIT_PARAMETER, Collections.singleton(Integer.toString(limit)));
        queryParameters.put(OFFSET_PARAMETER, Collections.singleton(Integer.toString(offset)));
        return requestBuilder;
    }

    public static Request.Builder createDefaultBuilder() {
        return new Request.Builder()
                   .acceptMimeType(DEFAULT_MIME_TYPE)
                   .method(HttpMethod.GET);
    }

}
