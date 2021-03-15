/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.decision;

public class BlackDuckDecision {
    private boolean shouldRun;
    private boolean isOffline;

    public BlackDuckDecision(final boolean shouldRun, final boolean isOffline) {
        this.shouldRun = shouldRun;
        this.isOffline = isOffline;
    }

    public static BlackDuckDecision skip() {
        return new BlackDuckDecision(false, true);
    }

    public static BlackDuckDecision runOffline() {
        return new BlackDuckDecision(true, true);
    }

    public static BlackDuckDecision runOnline() {
        return new BlackDuckDecision(true, false);
    }

    public boolean shouldRun() {
        return shouldRun;
    }

    public boolean isOffline() {
        return isOffline;
    }
}
