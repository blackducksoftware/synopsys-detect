package com.synopsys.integration.detect.battery.docker;

import com.synopsys.integration.detect.battery.util.DockerTestAssertions;

public class DockerDetectResult {
    private int exitCode;
    private String detectLogs;

    public DockerDetectResult(final int exitCode, final String detectLogs) {
        this.exitCode = exitCode;
        this.detectLogs = detectLogs;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getDetectLogs() {
        return detectLogs;
    }

    public DockerTestAssertions assertions() {
        return new DockerTestAssertions(this);
    }
}
