package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RapidModeStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }

    public void runOnline(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult) throws OperationException {
        operationRunner.phoneHome(blackDuckRunData);
        Optional<File> rapidScanConfig = operationRunner.findRapidScanConfig();
        rapidScanConfig.ifPresent(config -> logger.info("Found rapid scan config file: {}", config));
        List<HttpUrl> rapidScanUrls = operationRunner.performRapidUpload(blackDuckRunData, bdioResult, rapidScanConfig.orElse(null));
        List<DeveloperScanComponentResultView> rapidResults = operationRunner.waitForRapidResults(blackDuckRunData, rapidScanUrls);
        File jsonFile = operationRunner.generateRapidJsonFile(projectVersion, rapidResults);
        RapidScanResultSummary summary = operationRunner.logRapidReport(rapidResults);
        operationRunner.publishRapidResults(jsonFile, summary);
    }
}
