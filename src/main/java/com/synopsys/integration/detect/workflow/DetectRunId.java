package com.synopsys.integration.detect.workflow;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.blackducksoftware.common.base.ExtraUUIDs;

public class DetectRunId {
    private final String id;
    private final String uuid;

    public static DetectRunId createDefault() {
        return new DetectRunId(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC)),
            generateUuid()
        );
    }

    public static String generateUuid() {
        return ExtraUUIDs.toUriString(UUID.randomUUID());
    }

    public DetectRunId(String id, String uuid) {
        this.id = id;
        this.uuid = uuid;
    }

    public String getRunId() {
        return this.id;
    }

    public String getUuid() {
        return uuid;
    }
}
