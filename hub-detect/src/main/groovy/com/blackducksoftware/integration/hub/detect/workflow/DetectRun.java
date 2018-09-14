package com.blackducksoftware.integration.hub.detect.workflow;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DetectRun {
    private final String id;
    public static DetectRun createDefault() {
        return new DetectRun(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC)));
    }
    public DetectRun(String id){
        this.id = id;
    }
    public String getRunId() {
        return this.id;
    }
}
