package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanRapidResult;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class RapidModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;
    private final Gson gson;
    private final DirectoryManager directoryManager;

    public RapidModeStepRunner(OperationRunner operationRunner, StepHelper stepHelper, Gson gson, DirectoryManager directoryManager) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
        this.gson = gson;
        this.directoryManager = directoryManager;
    }

    public void runOnline(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, BdioResult bdioResult,
            DockerTargetData dockerTargetData, Optional<String> scaaasFilePath) throws OperationException {
        operationRunner.phoneHome(blackDuckRunData);
        Optional<File> rapidScanConfig = operationRunner.findRapidScanConfig();
        String scanMode = blackDuckRunData.getScanMode().displayName();
        rapidScanConfig.ifPresent(config -> logger.info("Found " + scanMode.toLowerCase() + " scan config file: {}", config));

        String blackDuckUrl = blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().toString();
        List<HttpUrl> parsedUrls = new ArrayList<>();
        
        List<HttpUrl> uploadResultsUrls = operationRunner.performRapidUpload(blackDuckRunData, bdioResult, rapidScanConfig.orElse(null));
        
        if (uploadResultsUrls != null && uploadResultsUrls.size() > 0) {
            parsedUrls.addAll(uploadResultsUrls);
        }

        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> {
            logger.debug("Stateless scan signature scan detected.");

            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationRunner);
            SignatureScanOuputResult signatureScanOutputResult = signatureScanStepRunner
                    .runRapidSignatureScannerOnline(blackDuckRunData, projectVersion, dockerTargetData);

            parsedUrls.addAll(parseScanUrls(scanMode, signatureScanOutputResult, blackDuckUrl));
        });
        
        stepHelper.runToolIfIncluded(DetectTool.BINARY_SCAN, "Binary Scanner", () -> {
            logger.debug("Stateless binary scan detected.");
            
            // Check if this is an SCA environment. Stateless Binary Scans are only supported there.
            if (scaaasFilePath.isPresent()) {
                invokeBdbaRapidScan(blackDuckRunData, projectVersion, blackDuckUrl, parsedUrls, false, scaaasFilePath.get());
            } else {
                logger.debug("Stateless binary scan detected but no detect.scaaas.scan.path specified, skipping.");
            }
        });
        
        stepHelper.runToolIfIncluded(DetectTool.CONTAINER_SCAN, "Container Scanner", () -> {
            logger.debug("Stateless container scan detected.");
            
            // Check if this is an SCA environment. Stateless Container Scans are only supported there.
            if (scaaasFilePath.isPresent()) {
                invokeBdbaRapidScan(blackDuckRunData, projectVersion, blackDuckUrl, parsedUrls, true, scaaasFilePath.get());
            } else {
                logger.debug("Stateless container scan detected but no detect.scaaas.scan.path specified, skipping.");
            }
        });

        // Get info about any scans that were done
        BlackduckScanMode mode = blackDuckRunData.getScanMode();
        List<DeveloperScansScanView> rapidResults = operationRunner.waitForRapidResults(blackDuckRunData, parsedUrls, mode);

        // Generate a report, even an empty one if no scans were done as that is what previous detect versions did.
        File jsonFile = operationRunner.generateRapidJsonFile(projectVersion, rapidResults);
        RapidScanResultSummary summary = operationRunner.logRapidReport(rapidResults, mode);
        operationRunner.publishRapidResults(jsonFile, summary, mode);
    }

    private void invokeBdbaRapidScan(BlackDuckRunData blackDuckRunData, NameVersion projectVersion, String blackDuckUrl,
            List<HttpUrl> parsedUrls, boolean isContainerScan, String scaasFilePath)
            throws IntegrationException, IOException, InterruptedException, OperationException, DetectUserFriendlyException {
        // Generate the UUID we use to communicate with BDBA
        UUID bdbaScanId = UUID.randomUUID();
        
        RapidBdbaStepRunner rapidBdbaStepRunner = new RapidBdbaStepRunner(gson, bdbaScanId);
        rapidBdbaStepRunner.submitScan(isContainerScan, scaasFilePath);
        rapidBdbaStepRunner.pollForResults();
        rapidBdbaStepRunner.downloadAndExtractBdio(directoryManager, projectVersion);

        UUID bdScanId = operationRunner.initiateStatelessBdbaScan(blackDuckRunData);
        operationRunner.uploadBdioEntries(blackDuckRunData, bdScanId);

        // add this scan to the URLs to wait for
        parsedUrls.add(new HttpUrl(blackDuckUrl + "/api/developer-scans/" + bdScanId.toString()));
    }

    /**
     * The signature scanner only returns a high level success or failure to us. Details are in the
     * output directory's scanOutput.json. We need to crack that open to get the scanId so we can poll
     * for the true results from BlackDuck later.
     * 
     * @return a list of URLs that BlackDuck should poll for rapid signature scan results.
     */
    private List<HttpUrl> parseScanUrls(String scanMode, SignatureScanOuputResult signatureScanOutputResult, String blackDuckUrl) throws IOException, IntegrationException {
        List<ScanCommandOutput> outputs = signatureScanOutputResult.getScanBatchOutput().getOutputs();
        List<HttpUrl> parsedUrls = new ArrayList<>(outputs.size());
        
        for (ScanCommandOutput output : outputs) {
            try {
                File specificRunOutputDirectory = output.getSpecificRunOutputDirectory();
                String scanOutputLocation = specificRunOutputDirectory.toString() + "/output/scanOutput.json";
                Reader reader = Files.newBufferedReader(Paths.get(scanOutputLocation));

                SignatureScanRapidResult result = gson.fromJson(reader, SignatureScanRapidResult.class);

                HttpUrl url = new HttpUrl(blackDuckUrl + "/api/developer-scans/" + result.scanId);

                logger.info(scanMode + " mode signature scan URL: {}", url);
                parsedUrls.add(url);
            } catch (Exception e) {
                throw new IntegrationException("Unable to parse rapid signature scan results.");
            }
        }
        
        return parsedUrls;
    }
}
