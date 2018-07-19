package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DetectRunManager {
    private String runId;

    public void init() {
        runId = createRunId();
    }

    public String getRunId() {
        return runId;
    }

    private String createRunId() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
    }
}
