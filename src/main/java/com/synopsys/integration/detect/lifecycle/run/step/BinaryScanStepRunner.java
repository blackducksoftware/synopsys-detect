package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.util.NameVersion;

public class BinaryScanStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BinaryScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }

    public Optional<CodeLocationCreationData<BinaryScanBatchOutput>> runBinaryScan(
        DockerTargetData dockerTargetData,
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData
    )
        throws OperationException {
        Optional<File> binaryScanFile = determineBinaryScanFileTarget(dockerTargetData);
        if (binaryScanFile.isPresent()) {
            return Optional.of(operationRunner.uploadBinaryScanFile(binaryScanFile.get(), projectNameVersion, blackDuckRunData));
        } else {
            return Optional.empty();
        }
    }

    public Optional<File> determineBinaryScanFileTarget(DockerTargetData dockerTargetData) throws OperationException {
        BinaryScanOptions binaryScanOptions = operationRunner.calculateBinaryScanOptions();
        File binaryUpload = null;
        if (binaryScanOptions.getSingleTargetFilePath().isPresent()) {
            logger.info("Binary upload will upload single file.");
            binaryUpload = binaryScanOptions.getSingleTargetFilePath().get().toFile();
        } else if (binaryScanOptions.getFileFilter().isPresent()) {
            Optional<File> multipleUploadTarget = operationRunner.searchForBinaryTargets(
                binaryScanOptions.getFileFilter().get(),
                binaryScanOptions.getSearchDepth(),
                binaryScanOptions.isFollowSymLinks()
            );
            if (multipleUploadTarget.isPresent()) {
                binaryUpload = multipleUploadTarget.get();
            } else {
                operationRunner.publishBinaryFailure("Binary scanner did not find any files matching any pattern.");
            }
        } else if (dockerTargetData != null && dockerTargetData.getContainerFilesystem().isPresent()) {
            logger.info("Binary Scanner will upload docker container file system.");
            binaryUpload = dockerTargetData.getContainerFilesystem()
                .get();// Very important not to binary scan the same Docker output that we sig scanned (=codelocation name collision)
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
}
