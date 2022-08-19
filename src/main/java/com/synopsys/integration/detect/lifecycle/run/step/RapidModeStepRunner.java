package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;

    public RapidModeStepRunner(OperationRunner operationRunner, StepHelper stepHelper) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
    }

    public void runOnline(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult, DockerTargetData dockerTargetData) throws OperationException {
        operationRunner.phoneHome(blackDuckRunData);
        Optional<File> rapidScanConfig = operationRunner.findRapidScanConfig();
        rapidScanConfig.ifPresent(config -> logger.info("Found rapid scan config file: {}", config));
        
        stepHelper.runToolIfIncluded(DetectTool.DETECTOR, "detector", () -> {
            if (bdioResult.isNotEmpty()) {
                List<HttpUrl> rapidScanUrls = operationRunner.performRapidUpload(blackDuckRunData, bdioResult, rapidScanConfig.orElse(null));
                List<DeveloperScanComponentResultView> rapidResults = operationRunner.waitForRapidResults(blackDuckRunData, rapidScanUrls);
            
                File jsonFile = operationRunner.generateRapidJsonFile(projectVersion, rapidResults);
                RapidScanResultSummary summary = operationRunner.logRapidReport(rapidResults);
                operationRunner.publishRapidResults(jsonFile, summary);
            } else {
                logger.debug("No BDIO results to upload. Skipping.");
            }
        });
        
        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> {
            logger.debug("Rapid scan signature scan detected.");
            
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationRunner);
            SignatureScannerCodeLocationResult signatureScannerCodeLocationResult = signatureScanStepRunner.runSignatureScannerOnline(
                    blackDuckRunData,
                    projectVersion,
                    dockerTargetData
                );
//            CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();
//            codeLocationAccumulator.addWaitableCodeLocations(signatureScannerCodeLocationResult.getWaitableCodeLocationData());
//            String breakpoint = "";
            
            String hardcodedUrl = "https://localhost/api/developer-scans/80dde241-5964-486a-82c8-2031def411c3";
            HttpUrl rapidScanUrl = new HttpUrl(hardcodedUrl);
                    //getRapidSignatureScanUrlFromFile();
            List<HttpUrl> rapidScanUrls = new ArrayList<>();
            rapidScanUrls.add(rapidScanUrl);
            
            List<DeveloperScanComponentResultView> rapidResults = operationRunner.waitForRapidResults(blackDuckRunData, rapidScanUrls);
            
            File jsonFile = operationRunner.generateRapidJsonFile(projectVersion, rapidResults);
            RapidScanResultSummary summary = operationRunner.logRapidReport(rapidResults);
            operationRunner.publishRapidResults(jsonFile, summary);
            
        });
    }
}
