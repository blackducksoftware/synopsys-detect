package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;

import org.jetbrains.annotations.Nullable;

public class CleanupDecision {
    private final boolean shouldCleanup;
    private final boolean shouldPreserveScan;
    private final boolean shouldPreserveBdio;
    private final boolean shouldPreserveAirGap;

    @Nullable
    private final File airGapZip;

    public CleanupDecision(final boolean shouldCleanup, final boolean shouldPreserveScan, final boolean shouldPreserveBdio, final boolean shouldPreserveAirGap, final @Nullable File airGapZip) {
        this.shouldCleanup = shouldCleanup;
        this.shouldPreserveScan = shouldPreserveScan;
        this.shouldPreserveBdio = shouldPreserveBdio;
        this.shouldPreserveAirGap = shouldPreserveAirGap;
        this.airGapZip = airGapZip;
    }

    public static CleanupDecision skip() {
        return new CleanupDecision(false, false, false, false, null);
    }

    public boolean shouldCleanup() {
        return shouldCleanup;
    }

    public boolean shouldPreserveScan() {
        return shouldPreserveScan;
    }

    public boolean shouldPreserveBdio() {
        return shouldPreserveBdio;
    }

    public boolean shouldPreserveAirGap() {
        return shouldPreserveAirGap;
    }

    public File getAirGapZip() {
        return airGapZip;
    }
}
