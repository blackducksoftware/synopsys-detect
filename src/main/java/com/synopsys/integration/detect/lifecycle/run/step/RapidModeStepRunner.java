package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    private final OperationFactory operationFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RapidModeStepRunner(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public void runOnline(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult) throws OperationException {
        operationFactory.phoneHome(blackDuckRunData);
        Optional<File> rapidScanConfig = operationFactory.findRapidScanConfig();
        rapidScanConfig.ifPresent(config -> logger.info("Found rapid scan config file: " + config.toString()));
        List<HttpUrl> rapidScanUrls = operationFactory.performRapidUpload(blackDuckRunData, bdioResult, rapidScanConfig.orElse(null));
        List<DeveloperScanComponentResultView> rapidResults = operationFactory.waitForRapidResults(blackDuckRunData, rapidScanUrls);
        File jsonFile = operationFactory.generateRapidJsonFile(projectVersion, rapidResults);
        RapidScanResultSummary summary = operationFactory.logRapidReport(rapidResults);
        operationFactory.publishRapidResults(jsonFile, summary);
    }
}
