/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.connection;

import java.util.List;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.rest.proxy.ProxyInfo;

public class ConnectionDetails {
    private final ProxyInfo proxyInformation; // Not null because of NO_PROXY_INFO value.
    private final List<Pattern> ignoredProxyHostPatterns;
    private final Long timeout;
    private final Boolean alwaysTrust;

    public ConnectionDetails(@NotNull final ProxyInfo proxyInformation, @NotNull final List<Pattern> ignoredProxyHostPatterns, @NotNull final Long timeout, @NotNull final Boolean alwaysTrust) {
        this.proxyInformation = proxyInformation;
        this.ignoredProxyHostPatterns = ignoredProxyHostPatterns;
        this.timeout = timeout;
        this.alwaysTrust = alwaysTrust;
    }

    @NotNull
    public List<Pattern> getIgnoredProxyHostPatterns() {
        return ignoredProxyHostPatterns;
    }

    @NotNull
    public ProxyInfo getProxyInformation() {
        return proxyInformation;
    }

    @NotNull
    public Long getTimeout() {
        return timeout;
    }

    @NotNull
    public Boolean getAlwaysTrust() {
        return alwaysTrust;
    }
}

