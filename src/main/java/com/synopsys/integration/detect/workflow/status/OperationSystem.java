/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;

public class OperationSystem {
    private final Collection<Operation> operations = new LinkedList<>();
    private final StatusEventPublisher statusEventPublisher;

    public OperationSystem(StatusEventPublisher statusEventPublisher) {
        this.statusEventPublisher = statusEventPublisher;
    }

    public void publishOperations() {
        operations.forEach(this::publishOperationIssues);
        statusEventPublisher.publishOperationsComplete(operations);
    }

    private void publishOperationIssues(Operation operation) {
        if (operation.getErrorMessages().length > 0) {
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.EXCEPTION, operation.getName(), Arrays.asList(operation.getErrorMessages())));
        }
    }

    public Operation startOperation(String operationName, OperationType type) {
        return startOperation(operationName, type, null);
    }

    public Operation startOperation(String operationName, OperationType type, @Nullable String phoneHomeKey) {
        Operation operation = new Operation(operationName, type, phoneHomeKey);
        operations.add(operation);
        return operation;
    }

}
