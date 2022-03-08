package com.synopsys.integration.detect.configuration.connection;

import java.util.List;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class ConnectionDetails {
    private final Gson gson;
    private final ProxyInfo proxyInformation; // Not null because of NO_PROXY_INFO value.
    private final List<Pattern> ignoredProxyHostPatterns;
    private final Long timeout;
    private final Boolean alwaysTrust;

    public ConnectionDetails(
        @NotNull Gson gson,
        @NotNull ProxyInfo proxyInformation,
        @NotNull List<Pattern> ignoredProxyHostPatterns,
        @NotNull Long timeout,
        @NotNull Boolean alwaysTrust
    ) {
        this.gson = gson;
        this.proxyInformation = proxyInformation;
        this.ignoredProxyHostPatterns = ignoredProxyHostPatterns;
        this.timeout = timeout;
        this.alwaysTrust = alwaysTrust;
    }

    @NotNull
    public Gson getGson() {
        return gson;
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
