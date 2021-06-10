/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OperationSystem {
    private final Map<String, Operation> operationMap = new HashMap<>();
    private final StatusEventPublisher statusEventPublisher;

    public OperationSystem(StatusEventPublisher statusEventPublisher) {
        this.statusEventPublisher = statusEventPublisher;
    }

    public void publishOperations() {
        operationMap.values().forEach(this::publishOperation);
    }

    public void publishOperation(Operation operation) {
        if (operation.getErrorMessages().length > 0) {
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.EXCEPTION, operation.getName(), Arrays.asList(operation.getErrorMessages())));
        }
        statusEventPublisher.publishOperation(operation);
    }

    public Operation startOperation(String operationName, OperationType type) {
        Operation currentOperation = operationMap.computeIfAbsent(operationName, key -> createNewOperation(operationName, type));
        if (currentOperation.getEndTime().isPresent()) {
            publishOperation(currentOperation);
            currentOperation = createNewOperation(operationName, type);
            operationMap.put(operationName, currentOperation);
        }
        return currentOperation;
    }

    private Operation createNewOperation(String operationName, OperationType type) {
        return new Operation(operationName, type);
    }
}
