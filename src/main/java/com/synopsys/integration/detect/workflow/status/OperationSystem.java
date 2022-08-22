package com.synopsys.integration.detect.workflow.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.lifecycle.shutdown.ExceptionUtility;

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
        operation.getException().ifPresent(exception -> {
            List<String> messages = new ArrayList<>();
            messages.add(ExceptionUtility.summarizeException(exception));
            operation.getTroubleshootingDetails().ifPresent(details -> messages.add(details));
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.EXCEPTION, operation.getName(), messages));        });
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
