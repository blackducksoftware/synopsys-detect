/*
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.IOException;
import java.util.Optional;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.http.client.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.useragent.BlackDuckCommon;
import com.synopsys.integration.blackduck.useragent.UserAgentBuilder;
import com.synopsys.integration.blackduck.useragent.UserAgentItem;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.ErrorResponse;
import com.synopsys.integration.rest.response.Response;

@Deprecated
/**
 * @deprecated Only for use while we must continue to support detect.blackduck.signature.scanner.host.url
 */
public class FakeBlackDuckHttpClientWrapper implements BlackDuckHttpClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IntHttpClient httpClient;
    private final HttpUrl baseUrl;
    private final String userAgentString;

    public FakeBlackDuckHttpClientWrapper(IntHttpClient httpClient, HttpUrl baseUrl, UserAgentItem solutionUserAgentItem) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        UserAgentBuilder userAgentBuilder = new UserAgentBuilder();
        userAgentBuilder.addUserAgent(solutionUserAgentItem);
        userAgentBuilder.addUserAgent(BlackDuckCommon.createUserAgentItem());
        this.userAgentString = userAgentBuilder.createFullUserAgentString();
    }

    @Override
    public Response execute(Request request) throws IntegrationException {
        return httpClient.execute(request);
    }

    @Override
    public Optional<Response> executeGetRequestIfModifiedSince(Request request, long l) throws IntegrationException, IOException {
        return httpClient.executeGetRequestIfModifiedSince(request, l);
    }

    @Override
    public Response attemptAuthentication() throws IntegrationException {
        throw new UnsupportedOperationException("We can't attempt authentication - we are fake.");
    }

    @Override
    public boolean isAlreadyAuthenticated(HttpUriRequest httpUriRequest) {
        return false;
    }

    @Override
    public Optional<ErrorResponse> extractErrorResponse(String s) {
        return httpClient.extractErrorResponse(s);
    }

    @Override
    public void handleErrorResponse(HttpUriRequest httpUriRequest, Response response) {
        logger.error("HTTP {} {} response: {} {}", httpUriRequest.getMethod(), httpUriRequest.getURI(), response.getStatusCode(), response.getStatusMessage());
    }

    @Override
    public void throwExceptionForError(Response response) throws IntegrationException {
        String msg = String.format("HTTP response: %d %s", response.getStatusCode(), response.getStatusMessage());
        throw new IntegrationException(msg);
    }

    @Override
    public HttpUrl getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getUserAgentString() {
        return userAgentString;
    }

    @Override
    public HttpClientBuilder getHttpClientBuilder() {
        return httpClient.getClientBuilder();
    }

    @Override
    public int getTimeoutInSeconds() {
        return httpClient.getTimeoutInSeconds();
    }

    @Override
    public boolean isAlwaysTrustServerCertificate() {
        return httpClient.isAlwaysTrustServerCertificate();
    }

    @Override
    public ProxyInfo getProxyInfo() {
        return httpClient.getProxyInfo();
    }

    @Override
    public IntLogger getLogger() {
        return httpClient.getLogger();
    }
}
