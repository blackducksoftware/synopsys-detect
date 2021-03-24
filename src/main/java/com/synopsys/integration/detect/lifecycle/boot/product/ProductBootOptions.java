/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
