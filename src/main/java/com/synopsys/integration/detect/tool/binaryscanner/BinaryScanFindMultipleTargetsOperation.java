package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class BinaryScanFindMultipleTargetsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileFinder fileFinder;
    private final DirectoryManager directoryManager;

    public BinaryScanFindMultipleTargetsOperation(FileFinder fileFinder, DirectoryManager directoryManager) {
        this.fileFinder = fileFinder;
        this.directoryManager = directoryManager;
    }

    public Optional<File> searchForMultipleTargets(Predicate<File> fileFilter, boolean followSymLinks, int depth) throws DetectUserFriendlyException {
        List<File> multipleTargets = fileFinder.findFiles(directoryManager.getSourceDirectory(), fileFilter, followSymLinks, depth, false);
        if (multipleTargets.size() > 0) {
            logger.info("Binary scan found {} files to archive for binary scan upload.", multipleTargets.size());
            return Optional.of(zipFilesForUpload(directoryManager.getSourceDirectory(), multipleTargets));
        } else {
            return Optional.empty();
        }
    }

    private File zipFilesForUpload(File sourceDir, List<File> multipleTargets) throws DetectUserFriendlyException {
        try {
            String zipPath = "binary-upload.zip";
            File zip = new File(directoryManager.getBinaryOutputDirectory(), zipPath);
            Map<String, Path> uploadTargets = collectUploadTargetsByRelPath(sourceDir, multipleTargets);
            DetectZipUtil.zip(zip, uploadTargets);
            logger.info("Binary scan created the following zip for upload: " + zip.toPath());
            return zip;
        } catch (IOException e) {
            throw new DetectUserFriendlyException("Unable to create binary scan archive for upload.", e, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    @NotNull
    private Map<String, Path> collectUploadTargetsByRelPath(File sourceDir, List<File> multipleTargets) {
        Path sourcePath = sourceDir.toPath();
        Map<String, Path> uploadTargets = new HashMap<>(multipleTargets.size());
        for (File fileToAdd : multipleTargets) {
            Path pathToAdd = fileToAdd.toPath();
            Path relativePath = sourcePath.relativize(pathToAdd);
            uploadTargets.put(relativePath.toString(), pathToAdd);
        }
        return uploadTargets;
    }

}
