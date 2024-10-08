package com.blackduck.integration.detect.workflow;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DetectRunId {
    private final String id;
    private String integratedMatchingCorrelationId;

    public static DetectRunId createDefault() {
        return new DetectRunId(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC)),
            generateIntegratedMatchingCorrelationId()
        );
    }

    public static String generateIntegratedMatchingCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public DetectRunId(String id, String integratedMatchingCorrelationId) {
        this.id = id;
        this.integratedMatchingCorrelationId = integratedMatchingCorrelationId;
    }

    public String getRunId() {
        return this.id;
    }

    public String getIntegratedMatchingCorrelationId() {
        return integratedMatchingCorrelationId;
    }
    
    public void stripCorrelationiD(String reason) {
        integratedMatchingCorrelationId = "";
    }
}
