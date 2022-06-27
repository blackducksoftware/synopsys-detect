package com.synopsys.integration.detect.tool.iac;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class IacScanReport {
    private final String scanTarget;
    @Nullable
    private final String errorMessage;
    @Nullable
    private final String codeLocationName;

    public static IacScanReport SUCCESS_OFFLINE(File scanTarget) {
        return new IacScanReport(scanTarget.getAbsolutePath(), null, null);
    }

    public static IacScanReport SUCCESS_ONLINE(File scanTarget, String codeLocationName) {
        return new IacScanReport(scanTarget.getAbsolutePath(), null, codeLocationName);
    }

    public static IacScanReport FAILURE(File scanTarget, String errorMessage) {
        return new IacScanReport(scanTarget.getAbsolutePath(), errorMessage, null);
    }

    private IacScanReport(String scanTarget, @Nullable String errorMessage, @Nullable String codeLocationName) {
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
