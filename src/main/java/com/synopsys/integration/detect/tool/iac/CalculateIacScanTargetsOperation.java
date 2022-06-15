package com.synopsys.integration.detect.tool.iac;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class CalculateIacScanTargetsOperation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IacScanOptions iacScanOptions;
    private final DirectoryManager directoryManager;

    public CalculateIacScanTargetsOperation(IacScanOptions iacScanOptions, DirectoryManager directoryManager) {
        this.iacScanOptions = iacScanOptions;
        this.directoryManager = directoryManager;
    }

    public List<File> calculateIacScanTargets() {
        List<File> userProvidedTargets = iacScanOptions.getIacScanPaths().stream()
            .map(Path::toFile)
            .collect(Collectors.toList());
        if (userProvidedTargets.isEmpty()) {
            File sourceDir = directoryManager.getSourceDirectory();
            logger.info(String.format("No IaC scan targets provided - registering the source path %s to scan", sourceDir.getAbsolutePath()));
            return Collections.singletonList(directoryManager.getSourceDirectory());
        } else {
            return userProvidedTargets;
        }
    }
}
