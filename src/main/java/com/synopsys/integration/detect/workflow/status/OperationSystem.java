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

    public void beginOperation(String operationName) {
        startOperation(operationName);
    }

    public void completeWithSuccess(String operationName) {
        Operation operation = operationMap.computeIfAbsent(operationName, this::createNewOperation);
        operation.success();
    }

    public void completeWithFailure(String operationName) {
        Operation operation = operationMap.computeIfAbsent(operationName, this::createNewOperation);
        operation.fail();

    }

    public void finishOperation(String operationName) { //for use in finally blocks.
        Operation operation = operationMap.computeIfAbsent(operationName, this::createNewOperation);
        operation.finish();
    }

    public void completeWithError(String operationName, String... errorMessages) {
        Operation operation = operationMap.computeIfAbsent(operationName, this::createNewOperation);
        operation.error(errorMessages);
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

    public Operation startOperation(String operationName) {
        Operation currentOperation = operationMap.computeIfAbsent(operationName, this::createNewOperation);
        if (currentOperation.getEndTime().isPresent()) {
            publishOperation(currentOperation);
            currentOperation = createNewOperation(operationName);
            operationMap.put(operationName, currentOperation);
        }
        return currentOperation;
    }

    private Operation createNewOperation(String operationName) {
        return Operation.of(operationName);
    }
}
