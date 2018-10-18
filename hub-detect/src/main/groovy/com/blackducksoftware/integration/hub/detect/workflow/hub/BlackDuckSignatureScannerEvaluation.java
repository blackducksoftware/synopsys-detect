package com.blackducksoftware.integration.hub.detect.workflow.hub;

import com.synopsys.integration.blackduck.summary.Result;

public class BlackDuckSignatureScannerEvaluation {
    public String scanPath;
    public Result scanResult;
    public Exception scanException;
    public String scanMessage;
    public boolean scanFinished = false;
}
