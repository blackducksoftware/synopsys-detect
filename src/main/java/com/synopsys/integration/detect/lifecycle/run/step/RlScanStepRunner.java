package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio.proto.domain.ScanType;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.util.bdio.protobuf.DetectProtobufBdioHeaderUtil;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;

public class RlScanStepRunner {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NameVersion projectNameVersion;
    private final String projectGroupName;
    private UUID scanId;
    private final OperationRunner operationRunner;
    private final File rlRunDirectory;
    private final BlackDuckRunData blackDuckRunData;
    private String codeLocationName;
    private static final String STORAGE_UPLOAD_ENDPOINT = "/api/storage/rldata/";
    private static final String STORAGE_RL_CONTENT_TYPE = "application/vnd.blackducksoftware.rl-data-1+octet-stream";
    private static final BlackDuckVersion MIN_BLACK_DUCK_VERSION = new BlackDuckVersion(2024, 4, 0);
    
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
            File fileToUpload = new File(operationRunner.getRlScanFilePath().get());
            
            // Generate BDIO header and obtain scanID
            initiateScan(fileToUpload.length());
            
            // TODO start cleanup here
            logger.info("ReversingLabs scan initiated. Uploading file to scan.");
            uploadFileToStorageService(fileToUpload);
            
            // TODO perhaps publish an event

        } catch (IntegrationException | IOException | OperationException e) {
            operationRunner.publishContainerFailure(e);
            return Optional.empty();
        }

        return Optional.ofNullable(scanId);
    }
    
    public String getCodeLocationName() {
        return codeLocationName;
    }

    private void uploadFileToStorageService(File fileToUpload) throws IOException, OperationException, IntegrationException {
        String storageServiceEndpoint = String.join("", STORAGE_UPLOAD_ENDPOINT, scanId.toString());
        logger.debug("Uploading ReversingLabs file to storage endpoint: {}", storageServiceEndpoint);
        
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();

        HttpUrl postUrl = blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().appendRelativeUrl(storageServiceEndpoint);
        BlackDuckResponseRequest buildBlackDuckResponseRequest = new BlackDuckRequestBuilder()
            .postFile(fileToUpload, ContentType.create(STORAGE_RL_CONTENT_TYPE))
            .addHeader("fileName", fileToUpload.getName())
            .buildBlackDuckResponseRequest(postUrl);

        try (Response response = blackDuckApiClient.execute(buildBlackDuckResponseRequest)) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("ReversingLabs file uploaded to storage service.");
            } else {
                logger.trace("Unable to upload ReversingLabs file. {} {}", response.getStatusCode(), response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload ReversingLabs file. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        } catch (IntegrationException e) {
            logger.trace("Could not execute file upload request to storage service.");
            throw new IntegrationException("Could not execute file upload request to storage service.", e);
        } catch (IOException e) {
            logger.trace("I/O error occurred during file upload request.");
            throw new IOException("I/O error occurred during file upload request to storage service.", e);
        }
    }

    private void initiateScan(long fileSize) throws IOException, IntegrationException, OperationException {
        DetectProtobufBdioHeaderUtil detectProtobufBdioHeaderUtil = new DetectProtobufBdioHeaderUtil(
            UUID.randomUUID().toString(),
            ScanType.RL.name(),
            projectNameVersion,
            projectGroupName,
            codeLocationName,
            fileSize);
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
