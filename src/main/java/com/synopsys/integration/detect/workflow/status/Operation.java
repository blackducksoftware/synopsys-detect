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

    private final Instant startTime;
    @Nullable
    private Instant endTime;
    private final String name;
    private StatusType statusType;
    private Exception exception;
    @Nullable
    private String troubleshootingDetails;
    private final OperationType operationType;
    @Nullable
    private final String phoneHomeKey;

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
        this(Instant.now(), type, null, name, StatusType.SUCCESS, null, null);
    }

    protected Operation(String name, OperationType type, @Nullable String phoneHomeKey) {
        this(Instant.now(), type, null, name, StatusType.SUCCESS, phoneHomeKey, null);
    }

    protected Operation(
        Instant startTime,
        OperationType operationType,
        @Nullable Instant endTime,
        String name,
        StatusType statusType,
        @Nullable String phoneHomeKey,
        @Nullable Exception exception
    ) {
        this.startTime = startTime;
        this.operationType = operationType;
        this.endTime = endTime;
        this.name = name;
        this.statusType = statusType;
        this.phoneHomeKey = phoneHomeKey;
        this.exception = exception;
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

    public void error(Exception e) {
        this.statusType = StatusType.FAILURE;
        this.exception = e;
        this.troubleshootingDetails = null;
        finish();
    }

    public void error(Exception e, String troubleshootingDetails) {
        this.statusType = StatusType.FAILURE;
        this.exception = e;
        this.troubleshootingDetails = troubleshootingDetails;
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

    public Optional<Exception> getException() {return Optional.ofNullable(exception);}

    public Optional<String> getTroubleshootingDetails() {
        return Optional.ofNullable(troubleshootingDetails);
    }
    
    public OperationType getOperationType() {
        return operationType;
    }
}
