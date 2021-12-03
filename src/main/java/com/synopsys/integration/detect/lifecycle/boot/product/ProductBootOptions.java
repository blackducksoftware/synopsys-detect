package com.synopsys.integration.detect.lifecycle.boot.product;

public class ProductBootOptions {
    private final boolean ignoreConnectionFailures;
    private final boolean testConnections;

    public ProductBootOptions(boolean ignoreConnectionFailures, boolean testConnections) {
        this.ignoreConnectionFailures = ignoreConnectionFailures;
        this.testConnections = testConnections;
    }

    public boolean isIgnoreConnectionFailures() {
        return ignoreConnectionFailures;
    }

    public boolean isTestConnections() {
        return testConnections;
    }

}
