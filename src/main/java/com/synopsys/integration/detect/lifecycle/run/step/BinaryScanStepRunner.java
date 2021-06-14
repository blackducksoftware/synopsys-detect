package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.util.NameVersion;

public class BinaryScanStepRunner {
    private OperationFactory operationFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BinaryScanStepRunner(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public Optional<CodeLocationCreationData<? extends CodeLocationBatchOutput<?>>> runBinaryScan(final DockerTargetData dockerTargetData, final NameVersion projectNameVersion, final BlackDuckRunData blackDuckRunData)
        throws DetectUserFriendlyException {
        Optional<File> binaryScanFile = determineBinaryScanFileTarget(dockerTargetData);
        if (binaryScanFile.isPresent()) {
            return Optional.of(operationFactory.uploadBinaryScanFile(binaryScanFile.get(), projectNameVersion, blackDuckRunData));
        } else {
            return Optional.empty();
        }
    }

    public Optional<File> determineBinaryScanFileTarget(DockerTargetData dockerTargetData) throws DetectUserFriendlyException {
        BinaryScanOptions binaryScanOptions = operationFactory.calculateBinaryScanOptions();
        File binaryUpload = null;
        if (binaryScanOptions.getSingleTargetFilePath().isPresent()) {
            logger.info("Binary upload will upload single file.");
            binaryUpload = binaryScanOptions.getSingleTargetFilePath().get().toFile();
        } else if (binaryScanOptions.getMultipleTargetFileNamePatterns().stream().anyMatch(StringUtils::isNotBlank)) {
            Optional<File> multipleUploadTarget = operationFactory.searchForBinaryTargets(binaryScanOptions.getMultipleTargetFileNamePatterns(), binaryScanOptions.getSearchDepth());
            if (multipleUploadTarget.isPresent()) {
                binaryUpload = multipleUploadTarget.get();
            } else {
                operationFactory.publishBinaryFailure("Binary scanner did not find any files matching any pattern.");
            }
        } else if (dockerTargetData != null && dockerTargetData.getContainerFilesystem().isPresent()) {
            logger.info("Binary Scanner will upload docker container file system.");
            binaryUpload = dockerTargetData.getContainerFilesystem().get();// Very important not to binary scan the same Docker output that we sig scanned (=codelocation name collision)
        }

        if (binaryUpload == null) {
            logger.info("Binary scanner found nothing to upload.");
            return Optional.empty();
        } else if (binaryUpload.isFile() && binaryUpload.canRead()) {
            return Optional.of(binaryUpload);
        } else {
            operationFactory.publishBinaryFailure("Binary scan file did not exist, is not a file or can't be read.");
            return Optional.empty();
        }
    }
}
