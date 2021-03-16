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
import java.util.Optional;

import javax.annotation.Nullable;

public class Operation {
    public static String formatTimestamp(@Nullable Instant executionTime) {
        if (null == executionTime) {
            return "";
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC).format(executionTime.atOffset(ZoneOffset.UTC));
    }

    private Instant startTime;
    @Nullable
    private Instant endTime;
    private String name;
    private StatusType statusType;
    private String[] errorMessages;

    public static Operation of(String name) {
        return new Operation(name);
    }

    protected Operation(String name) {
        this(Instant.now(), null, name, StatusType.SUCCESS);
    }

    protected Operation(Instant startTime, @Nullable Instant endTime, String name, StatusType statusType, String... errorMessages) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.statusType = statusType;
        this.errorMessages = errorMessages;
    }

    public void success() {
        this.statusType = StatusType.SUCCESS;
        endTime = Instant.now();
    }

    public void fail() {
        this.statusType = StatusType.FAILURE;
        endTime = Instant.now();
    }

    public void error(String... errorMessages) {
        this.statusType = StatusType.FAILURE;
        this.errorMessages = errorMessages;
        endTime = Instant.now();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public String getName() {
        return name;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public String[] getErrorMessages() {
        return errorMessages;
    }
}
