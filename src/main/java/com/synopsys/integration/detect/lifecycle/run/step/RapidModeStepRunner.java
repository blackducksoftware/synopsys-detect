package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    OperationFactory operationFactory;

    public void runAll(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult) throws DetectUserFriendlyException, IOException {
        operationFactory.phoneHome(blackDuckRunData);
        List<DeveloperScanComponentResultView> results = operationFactory.performRapidScan(blackDuckRunData, bdioResult);
        File jsonFile = operationFactory.generateRapidJsonFile(projectVersion, results);
        RapidScanResultSummary summary = operationFactory.logRapidReport(results);
        operationFactory.publishRapidResults(jsonFile, summary);
    }
}
