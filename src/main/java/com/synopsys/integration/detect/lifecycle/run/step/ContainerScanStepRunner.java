package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.OperationException;
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
    private final File containerImage;
    private static final BlackDuckVersion MIN_BLACK_DUCK_VERSION = new BlackDuckVersion(2023, 10, 0);
    private static final String STORAGE_CONTAINERS_ENDPOINT = "/api/storage/containers/";
    private static final String STORAGE_IMAGE_CONTENT_TYPE = "application/vnd.blackducksoftware.container-scan-data-1+octet-stream";
    private static final String STORAGE_IMAGE_METADATA_CONTENT_TYPE = "application/vnd.blackducksoftware.container-scan-message-1+json";

    public ContainerScanStepRunner(OperationRunner operationRunner, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData, Gson gson)
        throws IntegrationException, OperationException {
        this.operationRunner = operationRunner;
        this.projectNameVersion = projectNameVersion;
        this.blackDuckRunData = blackDuckRunData;
        binaryRunDirectory = operationRunner.getDirectoryManager().getBinaryOutputDirectory();
        if (binaryRunDirectory == null || !binaryRunDirectory.exists()) {
            throw new IntegrationException("Binary run directory does not exist.");
        }
        projectGroupName = operationRunner.calculateProjectGroupOptions().getProjectGroup();
        containerImage = operationRunner.getContainerScanImage(gson, binaryRunDirectory);
    }

    public Optional<UUID> invokeContainerScanningWorkflow() {
        try {
            logger.debug("Determining if configuration is valid to run a container scan.");
            if (!isContainerScanEligible()) {
                logger.info("No container.scan.file.path property was provided. Skipping container scan.");
                return Optional.ofNullable(scanId);
            }
            if (!isBlackDuckVersionValid()) {
                String minBlackDuckVersion = String.join(".",
                    Integer.toString(MIN_BLACK_DUCK_VERSION.getMajor()),
                    Integer.toString(MIN_BLACK_DUCK_VERSION.getMinor()),
                    Integer.toString(MIN_BLACK_DUCK_VERSION.getPatch())
                );
                throw new IntegrationException("Container scan is only supported with BlackDuck version " + minBlackDuckVersion + " or greater. Container scan could not be run.");
            }
            if (!isContainerImageResolved()) {
                throw new IOException("Container image file path not resolved or file could not be downloaded. Container scan could not be run.");
            }

            initiateScan();
            logger.info("Container scan initiated.");
            uploadImageToStorageService();
            uploadImageMetadataToStorageService();
            operationRunner.publishContainerSuccess();
            logger.info("Container scan image uploaded successfully.");
        } catch (IntegrationException | IOException | OperationException e) {
            operationRunner.publishContainerFailure(e);
        }
        return Optional.ofNullable(scanId);
    }

    private boolean isContainerImageResolved() {
        return containerImage != null && containerImage.exists();
    }

    private boolean isContainerScanEligible() {
        return operationRunner.getContainerScanFilePath().isPresent();
    }

    private boolean isBlackDuckVersionValid() {
        Optional<BlackDuckVersion> blackDuckVersion = blackDuckRunData.getBlackDuckServerVersion();
        return blackDuckVersion.isPresent() && blackDuckVersion.get().isAtLeast(MIN_BLACK_DUCK_VERSION);
    }

    private String getContainerScanCodeLocationName() {
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
        if (scanId == null) {
            logger.warn("Scan ID was not found in the response from the server.");
            throw new IntegrationException("Scan ID was not found in the response from the server.");
        }
        String scanIdString = scanId.toString();
        logger.debug("Scan initiated with scan service. Scan ID received: {}", scanIdString);
    }

    public void uploadImageToStorageService() throws IOException, IntegrationException, OperationException {
        String storageServiceEndpoint = String.join("", STORAGE_CONTAINERS_ENDPOINT, scanId.toString());
        String operationName = "Upload Container Scan Image";
        logger.debug("Uploading container image artifact to storage endpoint: {}", storageServiceEndpoint);

        try (Response response = operationRunner.uploadFileToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            containerImage,
            STORAGE_IMAGE_CONTENT_TYPE,
            operationName
        )
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Container scan image uploaded to storage service.");
            } else {
                logger.trace("Unable to upload container image. {} {}", response.getStatusCode(), response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload container image. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        }
    }

    public void uploadImageMetadataToStorageService() throws IntegrationException, IOException {
        String storageServiceEndpoint = String.join("", STORAGE_CONTAINERS_ENDPOINT, scanId.toString(), "/message");
        String operationName = "Upload Container Scan Image Metadata JSON";
        logger.debug("Uploading container image metadata to storage endpoint: {}", storageServiceEndpoint);

        JsonObject imageMetadataObject = operationRunner.createContainerScanImageMetadata(scanId, projectNameVersion);

        try (Response response = operationRunner.uploadJsonToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            imageMetadataObject.toString(),
            STORAGE_IMAGE_METADATA_CONTENT_TYPE,
            operationName
        )
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Container scan image metadata uploaded to storage service.");
            } else {
                logger.trace("Unable to upload container image metadata." + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload container image metadata. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        }
    }
}
