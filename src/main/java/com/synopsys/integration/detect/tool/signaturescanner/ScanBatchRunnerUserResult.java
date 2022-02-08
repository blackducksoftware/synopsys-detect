package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;

public class ScanBatchRunnerUserResult {
    private final ScanBatchRunner scanBatchRunner;
    private final File installDirectory;

    public ScanBatchRunnerUserResult(ScanBatchRunner scanBatchRunner, File installDirectory) {
        this.scanBatchRunner = scanBatchRunner;
        this.installDirectory = installDirectory;
    }

    public static ScanBatchRunnerUserResult fromLocalInstall(ScanBatchRunner scanBatchRunner, File installDirectory) {
        return new ScanBatchRunnerUserResult(scanBatchRunner, installDirectory);
    }

    public static ScanBatchRunnerUserResult fromCustomUrl(ScanBatchRunner scanBatchRunner) {
        return new ScanBatchRunnerUserResult(scanBatchRunner, null);
    }

    public static ScanBatchRunnerUserResult none() {
        return new ScanBatchRunnerUserResult(null, null);
    }

    public Optional<ScanBatchRunner> getScanBatchRunner() {
        return Optional.ofNullable(scanBatchRunner);
    }

    public Optional<File> getInstallDirectory() {
        return Optional.ofNullable(installDirectory);
    }
}
