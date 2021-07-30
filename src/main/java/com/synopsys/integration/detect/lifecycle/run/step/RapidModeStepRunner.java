/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    OperationFactory operationFactory;

    public RapidModeStepRunner(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public void runOnline(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult) throws DetectUserFriendlyException, IOException {
        operationFactory.phoneHome(blackDuckRunData);
        RapidScanResult rapidScanResult = operationFactory.performRapidScan(blackDuckRunData, bdioResult);
        File jsonFile = operationFactory.generateRapidJsonFile(projectVersion, rapidScanResult.getComponentResultViews());
        RapidScanResultSummary summary = operationFactory.logRapidReport(rapidScanResult.getComponentResultViews());
        operationFactory.publishRapidResults(jsonFile, summary);
    }
}
