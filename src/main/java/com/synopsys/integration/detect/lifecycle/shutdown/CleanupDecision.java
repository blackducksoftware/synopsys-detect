package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;

import org.jetbrains.annotations.Nullable;

public class CleanupDecision {
    private final boolean shouldCleanup;
    private final boolean shouldPreserveScan;
    private final boolean shouldPreserveBdio;
    private final boolean shouldPreserveAirGap;
    private final boolean shouldPreserveSigma;

    @Nullable
    private final File airGapZip;

    public CleanupDecision(
        boolean shouldCleanup,
        boolean shouldPreserveScan,
        boolean shouldPreserveBdio,
        boolean shouldPreserveAirGap,
        boolean shouldPreserveSigma,
        @Nullable File airGapZip
    ) {
        this.shouldCleanup = shouldCleanup;
        this.shouldPreserveScan = shouldPreserveScan;
        this.shouldPreserveBdio = shouldPreserveBdio;
        this.shouldPreserveAirGap = shouldPreserveAirGap;
        this.shouldPreserveSigma = shouldPreserveSigma;
        this.airGapZip = airGapZip;
    }

    public static CleanupDecision skip() {
        return new CleanupDecision(false, false, false, false, false, null);
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

    public boolean shouldPreserveSigma() {
        return shouldPreserveSigma;
    }

    public File getAirGapZip() {
        return airGapZip;
    }
}
