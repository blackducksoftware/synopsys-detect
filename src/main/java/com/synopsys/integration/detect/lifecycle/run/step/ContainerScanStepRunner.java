package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.util.bdio.protobuf.DetectProtobufBdioHeaderUtil;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;

import com.google.gson.JsonObject;

public class ContainerScanStepRunner {

    private final OperationRunner operationRunner;
    private UUID scanId;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NameVersion projectNameVersion;
    private final String projectGroupName;
    private final BlackDuckRunData blackDuckRunData;
    private final File binaryRunDirectory;

    public ContainerScanStepRunner(OperationRunner operationRunner, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData) {
        this.operationRunner = operationRunner;
        this.projectNameVersion = projectNameVersion;
        this.blackDuckRunData = blackDuckRunData;
        binaryRunDirectory = operationRunner.getDirectoryManager().getBinaryOutputDirectory();
        projectGroupName = operationRunner.calculateProjectGroupOptions().getProjectGroup();
    }

    public UUID invokeContainerScanningWorkflow() throws IntegrationException, IOException {
        initiateScan();
        uploadImageToStorageService();
        uploadImageMetadataToStorageService();
        return scanId;
    }

    private String getContainerScanCodeLocationName() {
        File containerImage = operationRunner.getContainerScanImage();
        CodeLocationNameManager codeLocationNameManager = operationRunner.getCodeLocationNameManager();
        return codeLocationNameManager.createContainerScanCodeLocationName(containerImage, projectNameVersion.getName(), projectNameVersion.getVersion());
    }

    public void initiateScan() throws IOException, IntegrationException {

        DetectProtobufBdioHeaderUtil detectProtobufBdioHeaderUtil = new DetectProtobufBdioHeaderUtil(
            UUID.randomUUID().toString(),
            "CONTAINER",
            projectNameVersion,
            projectGroupName,
            getContainerScanCodeLocationName());
        File bdioHeaderFile = detectProtobufBdioHeaderUtil.createProtobufBdioHeader(binaryRunDirectory);
        scanId = operationRunner.uploadBdioHeaderToInitiateScan(blackDuckRunData, bdioHeaderFile);
        String scanIdString = scanId.toString();
        logger.debug("Scan initiated with scan service. Scan ID received: {}", scanIdString);
    }

    public void uploadImageToStorageService() throws IntegrationException {
        File containerImage = operationRunner.getContainerScanImage();
        String storageServiceEndpoint = String.join("", "/api/storage/containers/", scanId.toString());
        String storageServiceArtifactContentType = "application/vnd.blackducksoftware.container-scan-data-1+octet-stream";
        logger.debug("Uploading container image artifact to storage endpoint: {}", storageServiceEndpoint);

        try (Response response = operationRunner.uploadFileToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            containerImage,
            storageServiceArtifactContentType)
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Container scan image uploaded to storage service.");
            } else {
                logger.trace("Unable to upload container image. {} {}", response.getStatusCode(), response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload container image. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        } catch (IOException | IntegrationException e) {
            throw new IntegrationException(e);
        }
    }

    public void uploadImageMetadataToStorageService() throws IntegrationException {
        String storageServiceEndpoint = String.join("", "/api/storage/containers/", scanId.toString(), "/message");
        String storageServiceArtifactContentType = "application/vnd.blackducksoftware.container-scan-message-1+json";
        logger.debug("Uploading container image metadata to storage endpoint: {}", storageServiceEndpoint);

        JsonObject imageMetadataObject = operationRunner.createContainerScanImageMetadata(scanId, projectNameVersion);

        try (Response response = operationRunner.uploadJsonToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            imageMetadataObject.toString(),
            storageServiceArtifactContentType)
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Container scan image metadata uploaded to storage service.");
            } else {
                logger.trace("Unable to upload container image metadata." + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload container image metadata. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));

            }
        } catch (IOException | IntegrationException e) {
            throw new IntegrationException(e);
        }
    }
}
