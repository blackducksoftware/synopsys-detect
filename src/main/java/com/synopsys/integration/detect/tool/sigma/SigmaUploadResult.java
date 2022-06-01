package com.synopsys.integration.detect.tool.sigma;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

//TODO- should this class have a method wasSuccess?
public class SigmaUploadResult {
    private final int statusCode;
    @Nullable
    private final String errorMessage;

    public static SigmaUploadResult SUCCESS() {
        return new SigmaUploadResult(0, null);
    }

    public static SigmaUploadResult FAILURE(int sigmaExitCode, String errorMessage) {
        return new SigmaUploadResult(sigmaExitCode, errorMessage);
    }

    private SigmaUploadResult(int sigmaExitCode, String errorMessage) {
        this.statusCode = sigmaExitCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public boolean wasSuccessful() {
        return errorMessage != null;
    }
}
