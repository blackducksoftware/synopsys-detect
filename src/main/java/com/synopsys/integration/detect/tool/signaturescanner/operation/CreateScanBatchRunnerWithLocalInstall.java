/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class CreateScanBatchRunnerWithLocalInstall { //TODO: Should this even exist as a class?
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntEnvironmentVariables intEnvironmentVariables;
    private final ScanPathsUtility scanPathsUtility;
    private final ScanCommandRunner scanCommandRunner;

    public CreateScanBatchRunnerWithLocalInstall(final IntEnvironmentVariables intEnvironmentVariables, final ScanPathsUtility scanPathsUtility,
        final ScanCommandRunner scanCommandRunner) {
        this.intEnvironmentVariables = intEnvironmentVariables;
        this.scanPathsUtility = scanPathsUtility;
        this.scanCommandRunner = scanCommandRunner;
    }

    public ScanBatchRunner createScanBatchRunner(File installDirectory) throws DetectUserFriendlyException {
        return ScanBatchRunner.createWithNoInstaller(intEnvironmentVariables, installDirectory, scanPathsUtility, scanCommandRunner);
    }
}
