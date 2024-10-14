package com.blackduck.integration.detect.workflow;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DetectRunId {
    private final String id;
    private String correlationId;

    public static DetectRunId createDefault() {
        return new DetectRunId(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC)),
            correlationId()
        );
    }

    public static String correlationId() {
        return UUID.randomUUID().toString();
    }

    public DetectRunId(String id, String correlationId) {
        this.id = id;
        this.correlationId = correlationId;
    }

    public String getRunId() {
        return this.id;
    }

    public String getCorrelationId() {
        return correlationId;
    }
    
    public void stripCorrelationId() {
        correlationId = null;
    }
}
