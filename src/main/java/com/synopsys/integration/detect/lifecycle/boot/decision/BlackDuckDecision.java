/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckDecision {
    private boolean shouldRun;
    private boolean isOffline;
    private BlackduckScanMode scanMode;

    public BlackDuckDecision(final boolean shouldRun, final boolean isOffline, final BlackduckScanMode scanMode) {
        this.shouldRun = shouldRun;
        this.isOffline = isOffline;
        this.scanMode = scanMode;
    }

    public static BlackDuckDecision skip() {
        return new BlackDuckDecision(false, true, null);
    }

    public static BlackDuckDecision runOffline() {
        return new BlackDuckDecision(true, true, null);
    }

    public static BlackDuckDecision runOnline(BlackduckScanMode scanMode) {
        return new BlackDuckDecision(true, false, scanMode);
    }

    public boolean shouldRun() {
        return shouldRun;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public BlackduckScanMode scanMode() {
        return scanMode;
    }
}
