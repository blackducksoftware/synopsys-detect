package com.synopsys.integration.detectable.detectables.gradle.inspection.inspector;

public class GradleInspectorProxyInfo {
    private final String proxyHost;
    private final String proxyPort;
    private final String nonProxyHosts;

    public GradleInspectorProxyInfo(final String proxyHost, final String proxyPort, final String nonProxyHosts) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.nonProxyHosts = nonProxyHosts;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getNonProxyHosts() {
        return nonProxyHosts;
    }
}
