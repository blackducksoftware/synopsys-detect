package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.io.File;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class CreateScanBatchRunnerWithLocalInstall { //TODO: Should this even exist as a class?
    private final IntEnvironmentVariables intEnvironmentVariables;
    private final ScanPathsUtility scanPathsUtility;
    private final ScanCommandRunner scanCommandRunner;

    public CreateScanBatchRunnerWithLocalInstall(IntEnvironmentVariables intEnvironmentVariables, ScanPathsUtility scanPathsUtility, ScanCommandRunner scanCommandRunner) {
        this.intEnvironmentVariables = intEnvironmentVariables;
        this.scanPathsUtility = scanPathsUtility;
        this.scanCommandRunner = scanCommandRunner;
    }

    public ScanBatchRunner createScanBatchRunner(File installDirectory) {
        return ScanBatchRunner.createWithNoInstaller(intEnvironmentVariables, scanPathsUtility, scanCommandRunner, installDirectory);
    }

}
