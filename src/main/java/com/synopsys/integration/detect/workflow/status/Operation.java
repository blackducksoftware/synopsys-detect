/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Operation {
    public static String formatExecutionTime(Instant executionTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC).format(executionTime.atOffset(ZoneOffset.UTC));
    }

    private final Instant executionTime;
    private final String descriptionKey;
    private final StatusType statusType;

    public Operation(String descriptionKey, StatusType statusType) {
        this(Instant.now(), descriptionKey, statusType);
    }

    public Operation(Instant executionTime, String descriptionKey, StatusType statusType) {
        this.executionTime = executionTime;
        this.descriptionKey = descriptionKey;
        this.statusType = statusType;
    }

    public Instant getExecutionTime() {
        return executionTime;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public StatusType getStatusType() {
        return statusType;
    }
}
