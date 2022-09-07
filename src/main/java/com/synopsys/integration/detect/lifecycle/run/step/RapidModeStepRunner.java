package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanRapidResult;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;
    private final Gson gson;

    public RapidModeStepRunner(OperationRunner operationRunner, StepHelper stepHelper, Gson gson) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
        this.gson = gson;
    }

    public void runOnline(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult, DockerTargetData dockerTargetData) throws OperationException {
        operationRunner.phoneHome(blackDuckRunData);
        Optional<File> rapidScanConfig = operationRunner.findRapidScanConfig();
        rapidScanConfig.ifPresent(config -> logger.info("Found rapid scan config file: {}", config));
        
        String blackDuckUrl = blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().toString();
        
        stepHelper.runToolIfIncluded(DetectTool.DETECTOR, "detector", () -> {
            if (bdioResult.isNotEmpty()) {
                List<HttpUrl> rapidScanUrls = operationRunner.performRapidUpload(blackDuckRunData, bdioResult, rapidScanConfig.orElse(null));
                List<DeveloperScansScanView> rapidResults = operationRunner.waitForRapidResults(blackDuckRunData, rapidScanUrls);
            
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
            SignatureScanOuputResult signatureScanOutputResult = signatureScanStepRunner.runRapidSignatureScannerOnline(
                    blackDuckRunData,
                    projectVersion,
                    dockerTargetData
                );
            
            List<HttpUrl> parsedUrls = parseScanUrls(signatureScanOutputResult, blackDuckUrl);
            
            List<DeveloperScansScanView> rapidResults = operationRunner.waitForRapidResults(blackDuckRunData, parsedUrls);
            
            File jsonFile = operationRunner.generateRapidJsonFile(projectVersion, rapidResults);
            RapidScanResultSummary summary = operationRunner.logRapidReport(rapidResults);
            operationRunner.publishRapidResults(jsonFile, summary);
            
        });
    }

    private List<HttpUrl> parseScanUrls(SignatureScanOuputResult signatureScanOutputResult, String blackDuckUrl) throws IOException, IntegrationException {
        List<ScanCommandOutput> outputs = signatureScanOutputResult.getScanBatchOutput().getOutputs();
        List<HttpUrl> parsedUrls = new ArrayList<>(outputs.size());
        
        for (ScanCommandOutput output : outputs) {
            File specificRunOutputDirectory = output.getSpecificRunOutputDirectory();
            String scanOutputLocation = specificRunOutputDirectory.toString() + "/output/scanOutput.json";
            Reader reader = Files.newBufferedReader(Paths.get(scanOutputLocation));

            SignatureScanRapidResult result = gson.fromJson(reader, SignatureScanRapidResult.class);

            parsedUrls.add(new HttpUrl(blackDuckUrl + "/api/developer-scans/" + result.scanId));
        }
        
        return parsedUrls;
    }
}
