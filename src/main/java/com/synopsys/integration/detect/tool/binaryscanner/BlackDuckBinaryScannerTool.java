/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerTool {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckBinaryScannerTool.class);
    private static final String STATUS_KEY = "BINARY_SCAN";

    private final CodeLocationNameManager codeLocationNameManager;
    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final BinaryScanOptions binaryScanOptions;
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final EventSystem eventSystem;

    public BlackDuckBinaryScannerTool(final EventSystem eventSystem, final CodeLocationNameManager codeLocationNameManager, final DirectoryManager directoryManager, final FileFinder fileFinder, final BinaryScanOptions binaryScanOptions,
        final BlackDuckServicesFactory blackDuckServicesFactory) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.binaryScanOptions = binaryScanOptions;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.eventSystem = eventSystem;
    }

    public boolean shouldRun() {
        if (binaryScanOptions.getSingleTargetFilePath().isPresent()) {
            logger.info("Binary scan will upload the single provided binary file path.");
            return true;
        } else if (binaryScanOptions.getMultipleTargetFileNamePatterns().stream().anyMatch(StringUtils::isNotBlank)) {
            logger.info("Binary scan will upload all files in the source directory that match the provided name patterns.");
            return true;
        }
        return false;
    }

    public BinaryScanToolResult performBinaryScanActions(final NameVersion projectNameVersion) throws DetectUserFriendlyException {

        File binaryUpload = null;
        final Optional<Path> singleTargetFilePath = binaryScanOptions.getSingleTargetFilePath();
        if (singleTargetFilePath.isPresent()) {
            binaryUpload = singleTargetFilePath.get().toFile();
        } else if (binaryScanOptions.getMultipleTargetFileNamePatterns().stream().anyMatch(StringUtils::isNotBlank)) {
            final List<File> multipleTargets = fileFinder.findFiles(directoryManager.getSourceDirectory(), binaryScanOptions.getMultipleTargetFileNamePatterns(), 0);
            if (multipleTargets != null && multipleTargets.size() > 0) {
                logger.info("Binary scan found {} files to archive for binary scan upload.", multipleTargets.size());
                try {
                    final String zipPath = "binary-upload.zip";
                    final File zip = new File(directoryManager.getBinaryOutputDirectory(), zipPath);
                    final Map<String, Path> uploadTargets = multipleTargets.stream().collect(Collectors.toMap(File::getName, File::toPath));
                    DetectZipUtil.zip(zip, uploadTargets);
                    logger.info("Binary scan created the following zip for upload: " + zip.toPath());
                    binaryUpload = zip;
                } catch (final IOException e) {
                    throw new DetectUserFriendlyException("Unable to create binary scan archive for upload.", e, ExitCodeType.FAILURE_UNKNOWN_ERROR);
                }
            }
        }

        if (binaryUpload != null && binaryUpload.isFile() && binaryUpload.canRead()) {
            final String name = projectNameVersion.getName();
            final String version = projectNameVersion.getVersion();
            final BinaryScanUploadService uploadService = blackDuckServicesFactory.createBinaryScanUploadService();
            final CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = uploadBinaryScanFile(uploadService, binaryUpload, name, version);
            return BinaryScanToolResult.SUCCESS(codeLocationCreationData);
        } else {
            logger.warn("Binary scan file did not exist, is not a file or can't be read.");
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, STATUS_KEY));
            return BinaryScanToolResult.FAILURE();
        }
    }

    public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFile(final BinaryScanUploadService binaryScanUploadService, final File binaryScanFile, final String projectName, final String projectVersionName)
        throws DetectUserFriendlyException {
        final String prefix = binaryScanOptions.getCodeLocationPrefix();
        final String suffix = binaryScanOptions.getCodeLocationSuffix();
        final String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(binaryScanFile, projectName, projectVersionName, prefix, suffix);
        try {
            logger.info("Preparing to upload binary scan file: " + codeLocationName);
            final BinaryScan binaryScan = new BinaryScan(binaryScanFile, projectName, projectVersionName, codeLocationName);
            final BinaryScanBatch binaryScanBatch = new BinaryScanBatch(binaryScan);
            final CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);

            final BinaryScanBatchOutput binaryScanBatchOutput = codeLocationCreationData.getOutput();
            binaryScanBatchOutput.throwExceptionForError(new Slf4jIntLogger(logger));

            logger.info("Successfully uploaded binary scan file: " + codeLocationName);
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.SUCCESS));
            return codeLocationCreationData;
        } catch (final IntegrationException e) {
            logger.error("Failed to upload binary scan file: " + e.getMessage());
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        }
    }
}
