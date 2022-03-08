package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.common.util.finder.FileFinder;

public class RapidModeConfigFindOperation {
    private static final String CONFIG_FILE_NAME = ".bd-rapid-scan.yaml";
    private final FileFinder fileFinder;

    public RapidModeConfigFindOperation(FileFinder fileFinder) {
        this.fileFinder = fileFinder;
    }

    public Optional<File> findRapidScanConfig(File sourceDirectory) {
        return Optional.ofNullable(fileFinder.findFile(sourceDirectory, CONFIG_FILE_NAME));
    }

}
