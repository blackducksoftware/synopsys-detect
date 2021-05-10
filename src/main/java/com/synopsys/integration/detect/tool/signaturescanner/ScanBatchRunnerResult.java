package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;

public class ScanBatchRunnerResult {
    private final ScanBatchRunner scanBatchRunner;
    private final File installDirectory;

    public ScanBatchRunnerResult(@NotNull final ScanBatchRunner scanBatchRunner, @NotNull final File installDirectory) {
        this.scanBatchRunner = scanBatchRunner;
        this.installDirectory = installDirectory;
    }

    @NotNull
    public ScanBatchRunner getScanBatchRunner() {
        return scanBatchRunner;
    }

    @NotNull
    public File getInstallDirectory() {
        return installDirectory;
    }
}
