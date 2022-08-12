package com.synopsys.integration.detect.workflow.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class DirectoryOptions {
    private final Path sourcePath;
    private final Path outputPath;
    private final Path bdioOutputPath;
    private final Path scanOutputPath;
    private final Path toolsOutputPath;
    private final Path impactOutputPath;
    private final Path statusJsonOutputPath;

    public DirectoryOptions(Path sourcePath, Path outputPath, Path bdioOutputPath, Path scanOutputPath, Path toolsOutputPath, Path impactOutputPath, Path statusJsonPath) throws IOException {
        this.sourcePath = toRealPath(sourcePath);
        this.outputPath = toRealPath(outputPath);
        this.bdioOutputPath = toRealPath(bdioOutputPath);
        this.scanOutputPath = toRealPath(scanOutputPath);
        this.toolsOutputPath = toRealPath(toolsOutputPath);
        this.impactOutputPath = toRealPath(impactOutputPath);
        this.statusJsonOutputPath = toRealPath(statusJsonPath);
    }

    public Optional<Path> getSourcePathOverride() {
        return Optional.ofNullable(sourcePath);
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

    public Optional<Path> getImpactOutputPathOverride() {
        return Optional.ofNullable(impactOutputPath);
    }

    public Optional<Path> getStatusJsonOutputPathOverride() {
        return Optional.ofNullable(statusJsonOutputPath);
    }

    public Optional<Path> getToolsOutputPath() {
        return Optional.ofNullable(toolsOutputPath);
    }

    @Nullable
    private Path toRealPath(@Nullable Path rawPath) throws IOException {
        if (rawPath == null) {
            return null;
        }
        return rawPath.toAbsolutePath();
    }
}
