/*
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
package com.synopsys.integration.polaris.common.configuration;

import java.util.function.BiConsumer;

import com.google.gson.Gson;
import com.synopsys.integration.builder.Buildable;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.polaris.common.service.PolarisServicesFactory;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.support.AuthenticationSupport;
import com.synopsys.integration.util.Stringable;

public class PolarisServerConfig extends Stringable implements Buildable {
    private final HttpUrl polarisUrl;
    private final int timeoutSeconds;
    private final String accessToken;
    private final ProxyInfo proxyInfo;
    private final boolean alwaysTrustServerCertificate;
    private final Gson gson;
    private final AuthenticationSupport authenticationSupport;

    public PolarisServerConfig(HttpUrl polarisUrl, int timeoutSeconds, String accessToken, ProxyInfo proxyInfo, boolean alwaysTrustServerCertificate, Gson gson,
        AuthenticationSupport authenticationSupport) {
        this.polarisUrl = polarisUrl;
        this.timeoutSeconds = timeoutSeconds;
        this.accessToken = accessToken;
        this.proxyInfo = proxyInfo;
        this.alwaysTrustServerCertificate = alwaysTrustServerCertificate;
        this.gson = gson;
        this.authenticationSupport = authenticationSupport;
    }

    public static PolarisServerConfigBuilder newBuilder() {
        return new PolarisServerConfigBuilder();
    }

    public AccessTokenPolarisHttpClient createPolarisHttpClient(IntLogger logger) {
        return new AccessTokenPolarisHttpClient(logger, timeoutSeconds, alwaysTrustServerCertificate, proxyInfo, polarisUrl, accessToken, gson, authenticationSupport);
    }

    public PolarisServicesFactory createPolarisServicesFactory(IntLogger logger) {
        return new PolarisServicesFactory(logger, createPolarisHttpClient(logger), gson);
    }

    public void populateEnvironmentVariables(BiConsumer<String, String> pairsConsumer) {
        pairsConsumer.accept(PolarisServerConfigBuilder.URL_KEY.getKey(), polarisUrl.toString());
        pairsConsumer.accept(PolarisServerConfigBuilder.ACCESS_TOKEN_KEY.getKey(), accessToken);
    }

    public HttpUrl getPolarisUrl() {
        return polarisUrl;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public boolean isAlwaysTrustServerCertificate() {
        return alwaysTrustServerCertificate;
    }

    public Gson getGson() {
        return gson;
    }

    public AuthenticationSupport getAuthenticationSupport() {
        return authenticationSupport;
    }

}
