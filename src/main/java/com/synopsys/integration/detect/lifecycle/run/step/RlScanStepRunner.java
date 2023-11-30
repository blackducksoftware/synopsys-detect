package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.util.bdio.protobuf.DetectProtobufBdioHeaderUtil;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;
import com.blackducksoftware.bdio.proto.domain.ScanType;

public class RlScanStepRunner {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NameVersion projectNameVersion;
    private final String projectGroupName;
    private UUID scanId;
    private final OperationRunner operationRunner;
    private final File rlRunDirectory;
    private final BlackDuckRunData blackDuckRunData;
    private String codeLocationName;
    private static final String STORAGE_CONTAINERS_ENDPOINT = "/api/storage/containers/";
    private static final BlackDuckVersion MIN_BLACK_DUCK_VERSION = new BlackDuckVersion(2023, 10, 0);
            // TODO true version but no servers with this right now, new BlackDuckVersion(2024, 4, 0);
    
    public RlScanStepRunner(OperationRunner operationRunner, BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion) {
        this.operationRunner = operationRunner;
        this.blackDuckRunData = blackDuckRunData;
        this.projectNameVersion = projectNameVersion;
        
        rlRunDirectory = operationRunner.getDirectoryManager().getReversingLabsOutputDirectory();
        projectGroupName = operationRunner.calculateProjectGroupOptions().getProjectGroup();
    }

    public Optional<UUID> invokeRlWorkflow() {
        try {
            logger.debug("Determining if configuration is valid to run a ReversingLabs scan.");
            if (!isReversingLabsEligible()) {
                logger.info("No detect.rl.scan.file.path property was provided. Skipping ReversingLabs scan.");
                return Optional.ofNullable(scanId);
            }
            if (!isBlackDuckVersionValid()) {
                String minBlackDuckVersion = String.join(".", 
                        Integer.toString(MIN_BLACK_DUCK_VERSION.getMajor()),
                        Integer.toString(MIN_BLACK_DUCK_VERSION.getMinor()),
                        Integer.toString(MIN_BLACK_DUCK_VERSION.getPatch()));
                throw new IntegrationException("ReversingLabs scan is only supported with BlackDuck version "
                        + minBlackDuckVersion + " or greater. ReversingLabs scan could not be run.");
            }

            codeLocationName = createCodeLocationName();
            
            // Generate BDIO header and obtain scanID
            initiateScan();
            
            logger.info("ReversingLabs scan initiated. Uploading file to scan.");
            uploadFileToStorageService();

        } catch (IntegrationException | IOException | OperationException e) {
            operationRunner.publishContainerFailure(e);
            return Optional.empty();
        }

        return Optional.ofNullable(scanId);
    }
    
    private void uploadFileToStorageService() {
        String storageServiceEndpoint = String.join("", STORAGE_CONTAINERS_ENDPOINT, scanId.toString());
        String operationName = "Upload ReversingLabs File";
        logger.debug("Uploading ReversingLabs target file to storage endpoint: {}", storageServiceEndpoint);

        File fileToUpload = new File(operationRunner.getRlScanFilePath().get());
        
        try (Response response = operationRunner.uploadFileToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            fileToUpload,
            STORAGE_IMAGE_CONTENT_TYPE,
//            operationName
        )
        ) {
//            if (response.isStatusCodeSuccess()) {
//                logger.debug("Container scan image uploaded to storage service.");
//            } else {
//                logger.trace("Unable to upload container image. {} {}", response.getStatusCode(), response.getStatusMessage());
//                throw new IntegrationException(String.join(" ", "Unable to upload container image. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
//            }
        }
    }

    // TODO very similar to container scan, might want to refactor?
    private void initiateScan() throws IOException, IntegrationException, OperationException {
        DetectProtobufBdioHeaderUtil detectProtobufBdioHeaderUtil = new DetectProtobufBdioHeaderUtil(
            UUID.randomUUID().toString(),
            // TODO this will need to be changed to a REVERSINGLABS scan when the scan container
            // can account for this.
            ScanType.BINARY.name(),
            projectNameVersion,
            projectGroupName,
            codeLocationName);
        File bdioHeaderFile = detectProtobufBdioHeaderUtil.createProtobufBdioHeader(rlRunDirectory);
        String operationName = "Upload ReversingLabs Scan BDIO Header to Initiate Scan";
        scanId = operationRunner.uploadBdioHeaderToInitiateScan(blackDuckRunData, bdioHeaderFile, operationName);
        if (scanId == null) {
            logger.warn("Scan ID was not found in the response from the server.");
            throw new IntegrationException("Scan ID was not found in the response from the server.");
        }
        String scanIdString = scanId.toString();
        logger.debug("Scan initiated with scan service. Scan ID received: {}", scanIdString);
    }

    private boolean isBlackDuckVersionValid() {
        Optional<BlackDuckVersion> blackDuckVersion = blackDuckRunData.getBlackDuckServerVersion();
        return blackDuckVersion.isPresent() && blackDuckVersion.get().isAtLeast(MIN_BLACK_DUCK_VERSION);
    }
    
    private boolean isReversingLabsEligible() {
        return operationRunner.getRlScanFilePath().isPresent();
    }
    
    private String createCodeLocationName() {
        CodeLocationNameManager codeLocationNameManager = operationRunner.getCodeLocationNameManager();
        File targetFile = new File(operationRunner.getRlScanFilePath().get());
        return codeLocationNameManager.createReversingLabsScanCodeLocationName(targetFile, projectNameVersion.getName(), projectNameVersion.getVersion());
    }

}
