package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.detect.lifecycle.OperationException;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.rest.response.Response;
import com.blackduck.integration.util.NameVersion;
import com.google.gson.JsonObject;

public class BdbaScanStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String STORAGE_BDBA_ENDPOINT = "/api/storage/bdba/";
    private static final String STORAGE_BDBA_CONTENT_TYPE = "application/vnd.blackducksoftware.bdba-scan-data-1+octet-stream";
    private static final String STORAGE_BDBA_MESSAGE_CONTENT_TYPE = "application/vnd.blackducksoftware.bdba-scan-message-1+json";

    public BdbaScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }
    
    public void runBdbaScan(NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData,
            Optional<File> scanFile, String scanId, String type) throws OperationException, IntegrationException {
        try {
            uploadBdbaFile(scanId, blackDuckRunData, scanFile.get());
        } catch (IOException e) {
            throw new IntegrationException("Unable to upload BDBA file.");
        }

        try {
            uploadMetadataToStorageService(scanId, projectNameVersion, blackDuckRunData, type);
        } catch (IOException e) {
            throw new IntegrationException("Unable to send BDBA metadata.");
        }
    }

    private void uploadBdbaFile(String scanId, BlackDuckRunData blackDuckRunData, File bdbaFile) throws IOException, OperationException, IntegrationException {
        String storageServiceEndpoint = String.join("", STORAGE_BDBA_ENDPOINT, scanId.toString());
        String operationName = "Upload BDBA Scan File";
        logger.debug("Uploading BDBA scan artifact to storage endpoint: {}", storageServiceEndpoint);

        try (Response response = operationRunner.uploadFileToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            bdbaFile,
            STORAGE_BDBA_CONTENT_TYPE,
            operationName
        )
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Scan file uploaded to storage service.");
            } else {
                logger.trace("Unable to upload scan file. {} {}", response.getStatusCode(), response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload scan file. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        }
    }

    private void uploadMetadataToStorageService(String scanId, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData, String type) throws IntegrationException, IOException, OperationException {
        String storageServiceEndpoint = String.join("", STORAGE_BDBA_ENDPOINT, scanId.toString(), "/message");
        String operationName = "Upload BDBA Scan Metadata JSON";
        logger.debug("Uploading BDBA metadata to storage endpoint: {}", storageServiceEndpoint);
        
        JsonObject metadataObject = operationRunner.createScanMetadata(UUID.fromString(scanId), projectNameVersion, type);

        try (Response response = operationRunner.uploadJsonToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            metadataObject.toString(),
            STORAGE_BDBA_MESSAGE_CONTENT_TYPE,
            operationName
        )
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("BDBA scan metadata uploaded to storage service.");
            } else {
                logger.trace("Unable to upload BDBA metadata." + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload BDBA metadata. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        }
    }
}
