package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

//TODO- should this class have a method wasSuccess?
public class SigmaReport {
    private final String scanTarget;
    @Nullable
    private final String errorMessage;

    public static SigmaReport SUCCESS(File scanTarget) {
        return new SigmaReport(scanTarget.getAbsolutePath(), null);
    }

    public static SigmaReport FAILURE(File scanTarget, String errorMessage) {
        return new SigmaReport(scanTarget.getAbsolutePath(), errorMessage);
    }

    private SigmaReport(String scanTarget, @Nullable String errorMessage) {
        this.scanTarget = scanTarget;
        this.errorMessage = errorMessage;
    }

    public String getScanTarget() {
        return scanTarget;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
