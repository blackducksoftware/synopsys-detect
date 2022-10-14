package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckDecision {
    private final boolean shouldRun;
    private final boolean isOffline;
    private final BlackduckScanMode scanMode;
    private final boolean hasSignatureScanner;

    public BlackDuckDecision(boolean shouldRun, boolean isOffline, BlackduckScanMode scanMode, boolean hasSigScan) {
        this.shouldRun = shouldRun;
        this.isOffline = isOffline;
        this.scanMode = scanMode;
        this.hasSignatureScanner = hasSigScan;
    }

    public static BlackDuckDecision skip() {
        return new BlackDuckDecision(false, true, null, false);
    }

    public static BlackDuckDecision runOffline() {
        // cannot run signature scan OFF line rapid or not... hasSigScan not really needed.
        return new BlackDuckDecision(true, true, null, false);
    }

    public static BlackDuckDecision runOnline(BlackduckScanMode scanMode, boolean hasSigScan) {
        return new BlackDuckDecision(true, false, scanMode, hasSigScan);
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

    public boolean hasSignatureScan() {
        return this.hasSignatureScanner;
    }
}
