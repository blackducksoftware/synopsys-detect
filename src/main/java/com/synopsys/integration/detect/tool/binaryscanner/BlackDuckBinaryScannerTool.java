/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerTool {
    private static final String BINARY_SCAN_FILE_UNREADABLE_MSG = "Binary scan file did not exist, is not a file or can't be read.";
    private final Logger logger = LoggerFactory.getLogger(BlackDuckBinaryScannerTool.class);
    private static final String STATUS_KEY = "BINARY_SCAN";
    private static final String OPERATION_NAME = "Black Duck Binary Scan";

    private final CodeLocationNameManager codeLocationNameManager;
    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final BinaryScanOptions binaryScanOptions;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final BinaryScanUploadService uploadService;
    private final OperationSystem operationSystem;

    public BlackDuckBinaryScannerTool(StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, CodeLocationNameManager codeLocationNameManager, DirectoryManager directoryManager, FileFinder fileFinder,
        BinaryScanOptions binaryScanOptions,
        BinaryScanUploadService uploadService, OperationSystem operationSystem) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.binaryScanOptions = binaryScanOptions;
        this.uploadService = uploadService;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.operationSystem = operationSystem;
    }

    private File zipFilesForUpload(List<File> multipleTargets) throws DetectUserFriendlyException {
        try {
            final String zipPath = "binary-upload.zip";
            File zip = new File(directoryManager.getBinaryOutputDirectory(), zipPath);
            Map<String, Path> uploadTargets = multipleTargets.stream().collect(Collectors.toMap(File::getName, File::toPath));
            DetectZipUtil.zip(zip, uploadTargets);
            logger.info("Binary scan created the following zip for upload: " + zip.toPath());
            return zip;
        } catch (IOException e) {
            operationSystem.completeWithFailure(OPERATION_NAME);
            throw new DetectUserFriendlyException("Unable to create binary scan archive for upload.", e, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    public BinaryScanToolResult performBinaryScanActions(@Nullable DockerTargetData dockerTargetData, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        File binaryUpload = null;
        Optional<Path> singleTargetFilePath = binaryScanOptions.getSingleTargetFilePath();
        if (singleTargetFilePath.isPresent()) {
            logger.info("Binary upload will upload single file.");
            binaryUpload = singleTargetFilePath.get().toFile();
        } else if (binaryScanOptions.getMultipleTargetFileNamePatterns().stream().anyMatch(StringUtils::isNotBlank)) {
            List<File> multipleTargets = fileFinder.findFiles(directoryManager.getSourceDirectory(), binaryScanOptions.getMultipleTargetFileNamePatterns(), binaryScanOptions.getSearchDepth());
            if (multipleTargets.size() > 0) {
                logger.info("Binary scan found {} files to archive for binary scan upload.", multipleTargets.size());
                binaryUpload = zipFilesForUpload(multipleTargets);
            } else {
                logger.warn("Binary scanner did not find any files matching pattern.");
                statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
                operationSystem.completeWithError(OPERATION_NAME, BINARY_SCAN_FILE_UNREADABLE_MSG);
                exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, STATUS_KEY);
                return BinaryScanToolResult.FAILURE();
            }
        } else if (dockerTargetData != null && dockerTargetData.getContainerFilesystem().isPresent()) {
            logger.info("Binary Scanner will upload docker container file system.");
            binaryUpload = dockerTargetData.getContainerFilesystem().get();
        } else if (dockerTargetData != null && dockerTargetData.getProvidedImageTar().isPresent()) {
            logger.info("Binary upload will docker provided image tar.");
            binaryUpload = dockerTargetData.getProvidedImageTar().get();
        }

        if (binaryUpload == null) {
            logger.info("Binary scanner found nothing to upload.");
            return BinaryScanToolResult.SKIPPED();
        }

        operationSystem.beginOperation(OPERATION_NAME);
        if (binaryUpload.isFile() && binaryUpload.canRead()) {
            String name = projectNameVersion.getName();
            String version = projectNameVersion.getVersion();
            CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = uploadBinaryScanFile(uploadService, binaryUpload, name, version);
            return BinaryScanToolResult.SUCCESS(codeLocationCreationData);
        } else {
            logger.warn(BINARY_SCAN_FILE_UNREADABLE_MSG);
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            operationSystem.completeWithError(OPERATION_NAME, BINARY_SCAN_FILE_UNREADABLE_MSG);
            exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, STATUS_KEY);
            return BinaryScanToolResult.FAILURE();
        }
    }

    public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFile(BinaryScanUploadService binaryScanUploadService, File binaryScanFile, String projectName, String projectVersionName)
        throws DetectUserFriendlyException {
        String prefix = binaryScanOptions.getCodeLocationPrefix();
        String suffix = binaryScanOptions.getCodeLocationSuffix();
        String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(binaryScanFile, projectName, projectVersionName, prefix, suffix);
        try {
            logger.info("Preparing to upload binary scan file: " + codeLocationName);
            BinaryScan binaryScan = new BinaryScan(binaryScanFile, projectName, projectVersionName, codeLocationName);
            BinaryScanBatch binaryScanBatch = new BinaryScanBatch(binaryScan);
            CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);

            BinaryScanBatchOutput binaryScanBatchOutput = codeLocationCreationData.getOutput();
            // The throwExceptionForError() in BinaryScanBatchOutput has a bug, so doing that work here
            throwExceptionForError(binaryScanBatchOutput);

            logger.info("Successfully uploaded binary scan file: " + codeLocationName);
            operationSystem.completeWithSuccess(OPERATION_NAME);
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
            return codeLocationCreationData;
        } catch (IntegrationException e) {
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            operationSystem.completeWithError(OPERATION_NAME, e.getMessage());
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        }
    }

    // BinaryScanBatchOutput used to do this, but our understanding of what needs to happen has been
    // changing rapidly. Once we're confident we know what it should do, it should presumably move back there.
    private void throwExceptionForError(BinaryScanBatchOutput binaryScanBatchOutput) throws BlackDuckIntegrationException {
        for (BinaryScanOutput binaryScanOutput : binaryScanBatchOutput) {
            if (binaryScanOutput.getResult() == Result.FAILURE) {
                // Black Duck responses are single-line message (loggable as-is), but nginx Bad Gateway responses are
                // multi-line html with the message embedded (that mess up the log).
                // cleanResponse() attempts to produce something reasonable to log in either case
                String cleanedBlackDuckResponse = cleanResponse(binaryScanOutput.getResponse());
                String uploadErrorMessage = String.format("Error when uploading binary scan: %s (Black Duck response: %s)",
                    binaryScanOutput.getErrorMessage().orElse(binaryScanOutput.getStatusMessage()),
                    cleanedBlackDuckResponse);
                logger.error(uploadErrorMessage);
                throw new BlackDuckIntegrationException(uploadErrorMessage);
            }
        }
    }

    private String cleanResponse(String response) {
        if (StringUtils.isBlank(response)) {
            return "";
        }
        String lengthLimitedResponse = StringUtils.substring(response, 0, 200);
        if (!lengthLimitedResponse.equals(response)) {
            lengthLimitedResponse = lengthLimitedResponse + "...";
        }
        String[] searchList = { "\n", "\r" };
        String[] replacementList = { " ", "" };
        return StringUtils.replaceEachRepeatedly(lengthLimitedResponse, searchList, replacementList);
    }
}
