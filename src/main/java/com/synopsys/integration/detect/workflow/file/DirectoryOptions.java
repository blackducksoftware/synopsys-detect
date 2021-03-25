/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.file;

import java.nio.file.Path;
import java.util.Optional;

public class DirectoryOptions {
    private final Path outputPath;
    private final Path bdioOutputPath;
    private final Path scanOutputPath;
    private final Path toolsOutputPath;

    public DirectoryOptions(Path outputPath, Path bdioOutputPath, Path scanOutputPath, Path toolsOutputPath) {
        this.outputPath = outputPath;
        this.bdioOutputPath = bdioOutputPath;
        this.scanOutputPath = scanOutputPath;
        this.toolsOutputPath = toolsOutputPath;
    }

    public Optional<Path> getOutputPathOverride() {
        return Optional.ofNullable(outputPath);
    }

    public Optional<Path> getBdioOutputPathOverride() {
        return Optional.ofNullable(bdioOutputPath);
    }

    public Optional<Path> getScanOutputPathOverride() {
        return Optional.ofNullable(scanOutputPath);
    }

    public Optional<Path> getToolsOutputPath() {
        return Optional.ofNullable(toolsOutputPath);
    }
}
