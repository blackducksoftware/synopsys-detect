package com.synopsys.integration.detect.tool.sigma;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class SigmaOptions {
    private final List<Path> sigmaPaths;
    @Nullable
    private final Path localSigmaPath;
    @Nullable
    private final String additionalArguments;
    @Nullable
    private final String codeLocationPrefix;
    @Nullable
    private final String codeLocationSuffix;

    public SigmaOptions(
        List<Path> sigmaPaths,
        Path localSigmaPath,
        String additionalArguments,
        @Nullable String codeLocationPrefix,
        @Nullable String codeLocationSuffix
    ) {
        this.sigmaPaths = sigmaPaths;
        this.localSigmaPath = localSigmaPath;
        this.additionalArguments = additionalArguments;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
    }

    public List<Path> getSigmaPaths() {
        return sigmaPaths;
    }

    public Optional<Path> getLocalSigmaPath() {
        return Optional.ofNullable(localSigmaPath);
    }

    public Optional<String> getAdditionalArguments() {
        return Optional.ofNullable(additionalArguments);
    }

    public Optional<String> getCodeLocationPrefix() {return Optional.ofNullable(codeLocationPrefix);}

    public Optional<String> getCodeLocationSuffix() {return Optional.ofNullable(codeLocationSuffix);}
}
