/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OperationSystem {
    private final Map<String, Operation> operationMap = new ConcurrentHashMap<>();
    private final StatusEventPublisher statusEventPublisher;

    public OperationSystem(StatusEventPublisher statusEventPublisher) {
        this.statusEventPublisher = statusEventPublisher;
    }

    public void beginOperation(String operationName) {
        startOperation(operationName);
    }

    public void completeWithSuccess(String operationName) {
        Operation operation = getOrCreateOperation(operationName);
        operation.success();
    }

    public void completeWithFailure(String operationName) {
        Operation operation = getOrCreateOperation(operationName);
        operation.fail();

    }

    public void completeWithError(String operationName, String... errorMessages) {
        Operation operation = getOrCreateOperation(operationName);
        operation.error(errorMessages);
    }

    public void finalizeOperations() {
        operationMap.values().forEach(this::finalizeOperation);
    }

    public void finalizeOperation(Operation operation) {
        if (operation.getErrorMessages().length > 0) {
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.EXCEPTION, operation.getName(), Arrays.asList(operation.getErrorMessages())));
        }
        statusEventPublisher.publishOperation(operation);
        operationMap.remove(operation.getName());
    }

    private Operation startOperation(String operationName) {
        Operation currentOperation = operationMap.computeIfAbsent(operationName, this::createNewOperation);
        if (currentOperation.getEndTime().isPresent()) {
            finalizeOperation(currentOperation);
            currentOperation = createNewOperation(operationName);
            operationMap.put(operationName, currentOperation);
        }
        return currentOperation;
    }

    private Operation getOrCreateOperation(String operationName) {
        Operation operation;
        if (!operationMap.containsKey(operationName)) {
            operation = startOperation(operationName);
        } else {
            operation = operationMap.get(operationName);
        }
        return operation;
    }

    private Operation createNewOperation(String operationName) {
        return Operation.of(operationName);
    }
}
