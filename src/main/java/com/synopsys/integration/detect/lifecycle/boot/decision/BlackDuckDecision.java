/*
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
    private boolean isRapid;

    public BlackDuckDecision(final boolean shouldRun, final boolean isOffline, final boolean isRapid) {
        this.shouldRun = shouldRun;
        this.isOffline = isOffline;
        this.isRapid = isRapid;
    }

    public static BlackDuckDecision skip() {
        return new BlackDuckDecision(false, true, false);
    }

    public static BlackDuckDecision runOffline() {
        return new BlackDuckDecision(true, true, false);
    }

    public static BlackDuckDecision runOnline(boolean isRapid) {
        return new BlackDuckDecision(true, false, isRapid);
    }

    public boolean shouldRun() {
        return shouldRun;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public boolean isRapid() {
        return isRapid;
    }
}
