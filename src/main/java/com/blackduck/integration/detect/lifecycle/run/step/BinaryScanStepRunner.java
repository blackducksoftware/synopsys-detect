package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.codelocation.CodeLocationCreationData;
import com.blackduck.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.blackduck.integration.detect.lifecycle.OperationException;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.data.DockerTargetData;
import com.blackduck.integration.detect.lifecycle.run.data.ScanCreationResponse;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.detect.lifecycle.run.operation.ScassOperationRunner;
import com.blackduck.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.blackduck.integration.detect.util.bdio.protobuf.DetectProtobufBdioHeaderUtil;
import com.blackduck.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.rest.response.Response;
import com.blackduck.integration.sca.upload.client.uploaders.ScassUploader;
import com.blackduck.integration.sca.upload.rest.model.response.BinaryFinishResponseContent;
import com.blackduck.integration.sca.upload.rest.status.BinaryUploadStatus;
import com.blackduck.integration.sca.upload.rest.status.UploadStatus;
import com.blackduck.integration.util.NameVersion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class BinaryScanStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Gson gson;
    
    private static final String STORAGE_CONTAINERS_ENDPOINT = "/api/storage/containers/";
    private static final String STORAGE_IMAGE_CONTENT_TYPE = "application/vnd.blackducksoftware.container-scan-data-1+octet-stream";
    private static final String STORAGE_IMAGE_METADATA_CONTENT_TYPE = "application/vnd.blackducksoftware.container-scan-message-1+json";

    public BinaryScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
        this.gson = new Gson();
    }
    
    public Optional<String> runScaasBinaryScan(
        DockerTargetData dockerTargetData,
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        Set<String> binaryTargets, 
        ScassOperationRunner scassUploadRunner
    )
        throws OperationException, IntegrationException {
        Optional<File> binaryScanFile = determineBinaryScanFileTarget(dockerTargetData, binaryTargets);
        if (binaryScanFile.isPresent()) {
            // call BlackDuck to create a scanID and determine where to upload the file
            ScanCreationResponse scanCreationResponse = initiateScan(projectNameVersion, binaryScanFile.get(), blackDuckRunData);
            
            String scanId = scanCreationResponse.getScanId();
            String uploadUrl = scanCreationResponse.getUploadUrl();
            
            if (StringUtils.isNotEmpty(uploadUrl)) {
                ScassUploader scaasScanUploader = scassUploadRunner.createScaasScanUploader();
                
                // TODO hardcode headers for now until we know if we need them
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-type", "application/octet-stream");
                
                UploadStatus status = scaasScanUploader.upload(uploadUrl, headers, binaryScanFile.get().toPath());
                
                if (status.isError()) {
                    handleUploadError(status);
                }
                
                // call /scans/{scanId}/scass-scan-processing to notify BlackDuck the file is uploaded
                scassUploadRunner.notifyUploadComplete(scanId);       
            } else {
                // TODO call new non-scass endpoint. For now mimic container scan
                try {
                    uploadNonScassFile(scanId, blackDuckRunData, binaryScanFile.get());
                } catch (IOException e) {
                    throw new IntegrationException("Unable to upload binary file.");
                }
                
                // TODO finish call, still piggybacking on container scan endpoints for now.
                // (send metadata for new non-SCASS approach to /api/storage/containers/{id}/message
                try {
                    uploadImageMetadataToStorageService(scanId, projectNameVersion, blackDuckRunData);
                } catch (IOException e) {
                    throw new IntegrationException("Unable to send binary metadata.");
                }
            }
            
            return Optional.of(scanId);
        } else {
            return Optional.empty();
        }
    }
    
    // TODO similar to containerScanStepRunner
    private void handleUploadError(UploadStatus status) throws IntegrationException {
        if (status == null) {
            throw new IntegrationException("Unexpected empty response attempting to upload binary image.");
        } else if (status.getException().isPresent()) {
            throw status.getException().get();      
        } else {
            throw new IntegrationException(String.format("Unable to upload binary image. Status code: {}. {}", status.getStatusCode(), status.getStatusMessage()));
        }  
    }

    // TODO similar to containerScanStepRunner
    private void uploadImageMetadataToStorageService(String scanId, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData) throws IntegrationException, IOException, OperationException {
        String storageServiceEndpoint = String.join("", STORAGE_CONTAINERS_ENDPOINT, scanId.toString(), "/message");
        String operationName = "Upload binary Scan Image Metadata JSON";
        logger.debug("Uploading binary image metadata to storage endpoint: {}", storageServiceEndpoint);
        
        JsonObject binaryMetadataObject = operationRunner.createScanMetadata(UUID.fromString(scanId), projectNameVersion, "BINARY");

        try (Response response = operationRunner.uploadJsonToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            binaryMetadataObject.toString(),
            STORAGE_IMAGE_METADATA_CONTENT_TYPE,
            operationName
        )
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("binary scan image metadata uploaded to storage service.");
            } else {
                logger.trace("Unable to upload binary image metadata." + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload binary image metadata. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        }
    }
    
    // TODO very similar to code in container scan step runner
    private void uploadNonScassFile(String scanId, BlackDuckRunData blackDuckRunData, File binaryFile) throws IOException, OperationException, IntegrationException {
        String storageServiceEndpoint = String.join("", STORAGE_CONTAINERS_ENDPOINT, scanId.toString());
        String operationName = "Upload Binary Scan Image";
        logger.debug("Uploading binary image artifact to storage endpoint: {}", storageServiceEndpoint);

        try (Response response = operationRunner.uploadFileToStorageService(
            blackDuckRunData,
            storageServiceEndpoint,
            binaryFile,
            STORAGE_IMAGE_CONTENT_TYPE,
            operationName
        )
        ) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Binary scan image uploaded to storage service.");
            } else {
                logger.trace("Unable to upload binary image. {} {}", response.getStatusCode(), response.getStatusMessage());
                throw new IntegrationException(String.join(" ", "Unable to upload binary image. Response code:", String.valueOf(response.getStatusCode()), response.getStatusMessage()));
            }
        }
    }
    
    public Optional<String> runBinaryScan(DockerTargetData dockerTargetData, NameVersion projectNameVersion,
            BlackDuckRunData blackDuckRunData, Set<String> binaryTargets)
            throws OperationException, IntegrationException {
        Optional<File> binaryScanFile = determineBinaryScanFileTarget(dockerTargetData, binaryTargets);
        if (binaryScanFile.isPresent()) {
            BinaryUploadStatus status = operationRunner.uploadBinaryScanFile(binaryScanFile.get(), projectNameVersion,
                    blackDuckRunData);
            return extractBinaryScanId(status);
        } else {
            return Optional.empty();
        }
    }

    public Optional<CodeLocationCreationData<BinaryScanBatchOutput>> runLegacyBinaryScan(
        DockerTargetData dockerTargetData,
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        Set<String> binaryTargets
    )
        throws OperationException {
        Optional<File> binaryScanFile = determineBinaryScanFileTarget(dockerTargetData, binaryTargets);
        if (binaryScanFile.isPresent()) {
            return Optional.of(operationRunner.uploadLegacyBinaryScanFile(binaryScanFile.get(), projectNameVersion, blackDuckRunData));
        } else {
            return Optional.empty();
        }
    }

    public Optional<File> determineBinaryScanFileTarget(DockerTargetData dockerTargetData, Set<String> binaryTargets) throws OperationException {
        BinaryScanOptions binaryScanOptions = operationRunner.calculateBinaryScanOptions();
        File binaryUpload = null;
        if (binaryScanOptions.getSingleTargetFilePath().isPresent()) {
            logger.info("Binary upload will upload single file.");
            binaryUpload = binaryScanOptions.getSingleTargetFilePath().get().toFile();
            operationRunner.updateBinaryUserTargets(binaryUpload);
        } else if (binaryScanOptions.getFileFilter().isPresent()) {
            Optional<File> multipleUploadTarget = operationRunner.searchForBinaryTargets(
                binaryScanOptions.getFileFilter().get(),
                binaryScanOptions.getSearchDepth(),
                binaryScanOptions.isFollowSymLinks()
            );
            if (multipleUploadTarget.isPresent()) {
                binaryUpload = multipleUploadTarget.get();
                List<File> multiTargets = operationRunner.getMultiBinaryTargets();
                multiTargets.forEach(operationRunner::updateBinaryUserTargets);
            } else {
                operationRunner.publishBinaryFailure("Binary scanner did not find any files matching any pattern.");
            }
        } else if (dockerTargetData != null && dockerTargetData.getContainerFilesystem().isPresent()) {
            logger.info("Binary Scanner will upload docker container file system.");
            binaryUpload = dockerTargetData.getContainerFilesystem()
                .get();// Very important not to binary scan the same Docker output that we sig scanned (=codelocation name collision)
        }

        if (binaryTargets != null && !binaryTargets.isEmpty()) {
            binaryUpload = operationRunner.collectBinaryTargets(binaryTargets).get();
        }

        if (binaryUpload == null) {
            logger.info("Binary scanner found nothing to upload.");
            return Optional.empty();
        } else if (binaryUpload.isFile() && binaryUpload.canRead()) {
            return Optional.of(binaryUpload);
        } else {
            operationRunner.publishBinaryFailure("Binary scan file did not exist, is not a file or can't be read.");
            return Optional.empty();
        }
    }
    
    public Optional<String> extractBinaryScanId(BinaryUploadStatus status) {
        try {
            BinaryFinishResponseContent response = status.getResponseContent().get();

            String location = response.getLocation();
            URI uri = new URI(location);
            String path = uri.getPath();
            String scanId = path.substring(path.lastIndexOf('/') + 1);
            return Optional.of(scanId);
        } catch (Exception e) {
            logger.warn("Unexpected response uploading binary, will be unable to wait for scan completion.");
            return Optional.empty();
        }
    }
    
    // TODO very similar to what is in ContainerScanStepRunner
    private ScanCreationResponse initiateScan(NameVersion projectNameVersion, File binaryFile, BlackDuckRunData blackDuckRunData) throws OperationException, IntegrationException {
        String projectGroupName = operationRunner.calculateProjectGroupOptions().getProjectGroup();
        
        CodeLocationNameManager codeLocationNameManager = operationRunner.getCodeLocationNameManager();
        String codeLocationName =  codeLocationNameManager.createBinaryScanCodeLocationName(binaryFile, projectNameVersion.getName(), projectNameVersion.getVersion());
        
        DetectProtobufBdioHeaderUtil detectProtobufBdioHeaderUtil = new DetectProtobufBdioHeaderUtil(
            UUID.randomUUID().toString(),
            "BINARY",
            projectNameVersion,
            projectGroupName,
            codeLocationName,
            binaryFile.length());
        
        File bdioHeaderFile;
        try {
            bdioHeaderFile = detectProtobufBdioHeaderUtil.createProtobufBdioHeader(
                    operationRunner.getDirectoryManager().getBinaryOutputDirectory());
        } catch (IOException e) {
            throw new IntegrationException("Unable to obtain binary run directory.");
        }
        
        String operationName = "Upload Binary Scan BDIO Header to Initiate Scan";
        //UUID scanId = operationRunner.uploadBdioHeaderToInitiateScan(blackDuckRunData, bdioHeaderFile, operationName);
        
        ScanCreationResponse scanCreationResponse 
            = operationRunner.uploadBdioHeaderToInitiateScassScan(blackDuckRunData, bdioHeaderFile, operationName, gson);
        
        // TODO likely need to improve error checking, etc here.
        String scanId = scanCreationResponse.getScanId();
        
        if (scanId == null) {
            logger.warn("Scan ID was not found in the response from the server.");
            throw new IntegrationException("Scan ID was not found in the response from the server.");
        }
        String scanIdString = scanId.toString();
        logger.debug("Scan initiated with scan service. Scan ID received: {}", scanIdString);
        
        return scanCreationResponse;
    }
}
