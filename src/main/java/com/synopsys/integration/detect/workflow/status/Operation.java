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

import org.jetbrains.annotations.Nullable;

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
    private OperationType operationType;
    private String phoneHomeKey;

    public static Operation of(String name) {
        return of(name, null);
    }

    public static Operation of(String name, @Nullable String phoneHomeKey) {
        return new Operation(name, OperationType.PUBLIC, phoneHomeKey);
    }

    public static Operation silentOf(String name) {
        return silentOf(name, null);
    }

    public static Operation silentOf(String name, @Nullable String phoneHomeKey) {
        return new Operation(name, OperationType.INTERNAL, phoneHomeKey);
    }

    protected Operation(String name, OperationType type) {
        this(Instant.now(), type, null, name, StatusType.SUCCESS, null);
    }

    protected Operation(String name, OperationType type, @Nullable String phoneHomeKey) {
        this(Instant.now(), type, null, name, StatusType.SUCCESS, phoneHomeKey);
    }

    protected Operation(Instant startTime, OperationType operationType, @Nullable Instant endTime, String name, StatusType statusType, @Nullable String phoneHomeKey, String... errorMessages) {
        this.startTime = startTime;
        this.operationType = operationType;
        this.endTime = endTime;
        this.name = name;
        this.statusType = statusType;
        this.phoneHomeKey = phoneHomeKey;
        this.errorMessages = errorMessages;
    }

    public void finish() {
        if (getEndTime().isPresent())
            return;

        endTime = Instant.now();
    }

    public void success() {
        this.statusType = StatusType.SUCCESS;
        finish();
    }

    public void fail() {
        this.statusType = StatusType.FAILURE;
        finish();
    }

    public void error(String... errorMessages) {
        this.statusType = StatusType.FAILURE;
        this.errorMessages = errorMessages;
        finish();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public Instant getEndTimeOrStartTime() {
        return getEndTime().orElse(getStartTime());
    }

    public String getName() {
        return name;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public Optional<String> getPhoneHomeKey() {
        return Optional.ofNullable(phoneHomeKey);
    }

    public String[] getErrorMessages() {
        return errorMessages;
    }

    public OperationType getOperationType() {
        return operationType;
    }
}
