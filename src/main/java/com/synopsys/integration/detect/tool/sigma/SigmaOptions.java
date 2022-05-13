package com.synopsys.integration.detect.tool.sigma;

import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class SigmaOptions {
    private final List<Path> sigmaPaths;
    @Nullable
    private final Path localSigmaPath;
    @Nullable
    private final String additionalArguments;

    public SigmaOptions(List<Path> sigmaPaths, Path localSigmaPath, String additionalArguments) {
        this.sigmaPaths = sigmaPaths;
        this.localSigmaPath = localSigmaPath;
        this.additionalArguments = additionalArguments;
    }

    public List<Path> getSigmaPaths() {
        return sigmaPaths;
    }

    public Path getLocalSigmaPath() {
        return localSigmaPath;
    }

    public String getAdditionalArguments() {
        return additionalArguments;
    }
}
