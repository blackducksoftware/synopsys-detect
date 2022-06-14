package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class SigmaReport {
    private final String scanTarget;
    @Nullable
    private final String errorMessage;
    @Nullable
    private final String codeLocationName;

    public static SigmaReport SUCCESS_OFFLINE(File scanTarget) {
        return new SigmaReport(scanTarget.getAbsolutePath(), null, null);
    }

    public static SigmaReport SUCCESS_ONLINE(File scanTarget, String codeLocationName) {
        return new SigmaReport(scanTarget.getAbsolutePath(), null, codeLocationName);
    }

    public static SigmaReport FAILURE(File scanTarget, String errorMessage) {
        return new SigmaReport(scanTarget.getAbsolutePath(), errorMessage, null);
    }

    private SigmaReport(String scanTarget, @Nullable String errorMessage, @Nullable String codeLocationName) {
        this.scanTarget = scanTarget;
        this.errorMessage = errorMessage;
        this.codeLocationName = codeLocationName;
    }

    public String getScanTarget() {
        return scanTarget;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public Optional<String> getCodeLocationName() {return Optional.ofNullable(codeLocationName);}
}
