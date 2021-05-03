/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DetectRunId {
    private final String id;

    public static DetectRunId createDefault() {
        return new DetectRunId(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC)));
    }

    public DetectRunId(String id) {
        this.id = id;
    }

    public String getRunId() {
        return this.id;
    }
}
