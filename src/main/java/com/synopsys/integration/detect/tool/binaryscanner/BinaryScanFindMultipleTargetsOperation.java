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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class BinaryScanFindMultipleTargetsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private FileFinder fileFinder;
    private DirectoryManager directoryManager;

    public BinaryScanFindMultipleTargetsOperation(final FileFinder fileFinder, final DirectoryManager directoryManager) {
        this.fileFinder = fileFinder;
        this.directoryManager = directoryManager;
    }

    public Optional<File> searchForMultipleTargets(List<String> patterns, int depth) throws DetectUserFriendlyException {
        List<File> multipleTargets = fileFinder.findFiles(directoryManager.getSourceDirectory(), patterns, depth);
        if (multipleTargets.size() > 0) {
            logger.info("Binary scan found {} files to archive for binary scan upload.", multipleTargets.size());
            return Optional.of(zipFilesForUpload(multipleTargets));
        } else {
            return Optional.empty();
        }
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
            throw new DetectUserFriendlyException("Unable to create binary scan archive for upload.", e, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

}
