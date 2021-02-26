/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.signaturescanner;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class SignatureScannerReport {
    private final SignatureScanPath signatureScanPath;
    private final StatusType statusType;
    @Nullable
    private final Integer exitCode;
    @Nullable
    private final Exception exception;
    @Nullable
    private final String errorMessage;
    private final boolean hasOutput;

    public static SignatureScannerReport create(SignatureScanPath signatureScanPath, @Nullable ScanCommandOutput scanCommandOutput) {
        StatusType statusType;

        if (scanCommandOutput == null || Result.FAILURE.equals(scanCommandOutput.getResult())) {
            statusType = StatusType.FAILURE;
        } else {
            statusType = StatusType.SUCCESS;
        }

        Optional<ScanCommandOutput> optionalOutput = Optional.ofNullable(scanCommandOutput);
        boolean hasOutput = optionalOutput.isPresent();
        Integer exitCode = optionalOutput
                               .map(ScanCommandOutput::getScanExitCode)
                               .filter(Optional::isPresent)
                               .map(Optional::get)
                               .orElse(null);
        Exception exception = optionalOutput
                                  .map(ScanCommandOutput::getException)
                                  .filter(Optional::isPresent)
                                  .map(Optional::get)
                                  .orElse(null);
        String errorMessage = optionalOutput
                                  .map(ScanCommandOutput::getErrorMessage)
                                  .filter(Optional::isPresent)
                                  .map(Optional::get)
                                  .orElse(null);

        return new SignatureScannerReport(signatureScanPath, statusType, exitCode, exception, errorMessage, hasOutput);
    }

    public SignatureScannerReport(SignatureScanPath signatureScanPath, StatusType statusType, @Nullable Integer exitCode, @Nullable Exception exception, @Nullable String errorMessage, boolean hasOutput) {
        this.signatureScanPath = signatureScanPath;
        this.statusType = statusType;
        this.exitCode = exitCode;
        this.exception = exception;
        this.errorMessage = errorMessage;
        this.hasOutput = hasOutput;
    }

    public SignatureScanPath getSignatureScanPath() {
        return signatureScanPath;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public boolean isSuccessful() {
        return StatusType.SUCCESS.equals(statusType);
    }

    public boolean isFailure() {
        return StatusType.FAILURE.equals(statusType);
    }

    public Optional<Integer> getExitCode() {
        return Optional.ofNullable(exitCode);
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public boolean hasOutput() {
        return hasOutput;
    }
}
