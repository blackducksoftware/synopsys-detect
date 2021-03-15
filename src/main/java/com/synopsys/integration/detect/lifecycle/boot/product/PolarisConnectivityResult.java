/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.product;

public class PolarisConnectivityResult {
    private final boolean successfullyConnected;

    //if failure, the following is populated
    private final String failureReason;

    private PolarisConnectivityResult(final boolean successfullyConnected, final String failureReason) {
        this.successfullyConnected = successfullyConnected;
        this.failureReason = failureReason;
    }

    public static PolarisConnectivityResult success() {
        return new PolarisConnectivityResult(true, null);
    }

    public static PolarisConnectivityResult failure(final String reason) {
        return new PolarisConnectivityResult(false, reason);
    }

    public boolean isSuccessfullyConnected() {
        return successfullyConnected;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
