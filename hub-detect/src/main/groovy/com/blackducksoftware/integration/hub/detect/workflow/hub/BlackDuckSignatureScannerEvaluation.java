package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.Optional;

import com.synopsys.integration.blackduck.summary.Result;

public class BlackDuckSignatureScannerEvaluation {
    public final String scanPath;
    public Result scanResult = Result.FAILURE;
    public Optional<Exception> scanException = Optional.empty();
    public Optional<String> scanMessage = Optional.empty();
    public Optional<Integer> exitCode = Optional.empty();
    public boolean scanFinished = false;

    public BlackDuckSignatureScannerEvaluation(String scanPath) {
        this.scanPath = scanPath;
    }
}
