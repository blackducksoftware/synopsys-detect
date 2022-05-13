package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class CalculateSigmaScanTargetsOperation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SigmaOptions sigmaOptions;
    private final DirectoryManager directoryManager;

    public CalculateSigmaScanTargetsOperation(SigmaOptions sigmaOptions, DirectoryManager directoryManager) {
        this.sigmaOptions = sigmaOptions;
        this.directoryManager = directoryManager;
    }

    public List<File> calculateSigmaScanTargets() {
        List<File> userProvidedTargets = sigmaOptions.getSigmaPaths().stream()
            .map(Path::toFile)
            .collect(Collectors.toList());
        if (userProvidedTargets.isEmpty()) {
            File sourceDir = directoryManager.getSourceDirectory();
            logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourceDir.getAbsolutePath()));
            return Collections.singletonList(directoryManager.getSourceDirectory());
        } else {
            return userProvidedTargets;
        }
    }
}
