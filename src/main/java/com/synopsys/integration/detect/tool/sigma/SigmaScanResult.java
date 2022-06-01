package com.synopsys.integration.detect.tool.sigma;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

//TODO- should this class have a method wasSuccess?
public class SigmaScanResult {
    @Nullable
    private final File resultsFile;
    private final int statusCode;
    @Nullable
    private final String errorMessage;

    public static SigmaScanResult SUCCESS(File resultsFile) {
        return new SigmaScanResult(resultsFile, 0, null);
    }

    public static SigmaScanResult FAILURE(int statusCode, String errorMessage) {
        return new SigmaScanResult(null, statusCode, errorMessage);
    }

    private SigmaScanResult(@Nullable File resultsFile, int statusCode, @Nullable String errorMessage) {
        this.resultsFile = resultsFile;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public Optional<File> getResultsFile() {
        return Optional.ofNullable(resultsFile);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public boolean wasSuccessful() {
        return resultsFile != null && errorMessage == null;
    }
}
