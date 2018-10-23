package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.util.Optional;

import com.synopsys.integration.blackduck.summary.Result;

public class BlackDuckSignatureScannerEvaluation {
    public String scanPath;
    public Result scanResult;
    public Optional<Exception> scanException;
    public Optional<String> scanMessage;
    public Optional<Integer> exitCode;
    public boolean scanFinished = false;
}
