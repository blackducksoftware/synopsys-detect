package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckDecision {
    private final boolean shouldRun;
    private final boolean isOffline;
    private final BlackduckScanMode scanMode;

    public BlackDuckDecision(boolean shouldRun, boolean isOffline, BlackduckScanMode scanMode) {
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
