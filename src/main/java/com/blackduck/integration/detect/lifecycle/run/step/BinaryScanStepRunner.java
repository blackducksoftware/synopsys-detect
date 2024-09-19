package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.blackduck.upload.rest.model.response.BinaryFinishResponseContent;
import com.synopsys.blackduck.upload.rest.status.BinaryUploadStatus;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.blackduck.integration.detect.lifecycle.OperationException;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.data.DockerTargetData;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BinaryScanStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BinaryScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }

    public Optional<String> runBinaryScan(
        DockerTargetData dockerTargetData,
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        Set<String> binaryTargets
    )
        throws OperationException, IntegrationException {
        Optional<File> binaryScanFile = determineBinaryScanFileTarget(dockerTargetData, binaryTargets);
        if (binaryScanFile.isPresent()) {
            BinaryUploadStatus status = operationRunner.uploadBinaryScanFile(binaryScanFile.get(), projectNameVersion, blackDuckRunData);
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
}
