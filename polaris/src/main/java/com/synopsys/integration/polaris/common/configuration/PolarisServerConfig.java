/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
