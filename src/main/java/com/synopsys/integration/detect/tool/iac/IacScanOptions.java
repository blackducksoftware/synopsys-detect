package com.synopsys.integration.detect.tool.iac;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class IacScanOptions {
    private final List<Path> iacScanPaths;
    @Nullable
    private final Path localIacScannerPath;
    @Nullable
    private final String additionalArguments;
    @Nullable
    private final String codeLocationPrefix;
    @Nullable
    private final String codeLocationSuffix;

    public IacScanOptions(
        List<Path> iacScanPaths,
        Path localIacScannerPath,
        String additionalArguments,
        @Nullable String codeLocationPrefix,
        @Nullable String codeLocationSuffix
    ) {
        this.iacScanPaths = iacScanPaths;
        this.localIacScannerPath = localIacScannerPath;
        this.additionalArguments = additionalArguments;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
    }

    public List<Path> getIacScanPaths() {
        return iacScanPaths;
    }

    public Optional<Path> getLocalIacScannerPath() {
        return Optional.ofNullable(localIacScannerPath);
    }

    public Optional<String> getAdditionalArguments() {
        return Optional.ofNullable(additionalArguments);
    }

    public Optional<String> getCodeLocationPrefix() {return Optional.ofNullable(codeLocationPrefix);}

    public Optional<String> getCodeLocationSuffix() {return Optional.ofNullable(codeLocationSuffix);}
}
