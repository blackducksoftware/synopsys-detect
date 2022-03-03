package com.synopsys.integration.detect.workflow.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class OperationSystemTest {
    private EventSystem eventSystem;
    private StatusEventPublisher statusEventPublisher;
    private List<DetectIssue> detectIssues = new ArrayList<>();
    private List<Operation> detectOperations = new ArrayList<>();

    @BeforeEach
    public void initTest() {
        eventSystem = new EventSystem();
        statusEventPublisher = new StatusEventPublisher(eventSystem);
        detectIssues = new ArrayList<>();
        detectOperations = new ArrayList<>();
        eventSystem.registerListener(Event.DetectOperationsComplete, detectOperations::addAll);
        eventSystem.registerListener(Event.Issue, detectIssues::add);
    }

    @Test
    public void testStartOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operationSystem.publishOperations();
        assertTrue(detectIssues.isEmpty());
        assertFalse(detectOperations.isEmpty());
        Operation publishedOperation = detectOperations.get(0);
        assertEquals(operationName, publishedOperation.getName());
        assertEquals(StatusType.SUCCESS, publishedOperation.getStatusType());
        assertNotNull(publishedOperation.getStartTime());
        assertFalse(publishedOperation.getEndTime().isPresent());
    }

    @Test
    public void testStartReplaceOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.fail();
        Operation operation2 = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operationSystem.publishOperations();
        Operation completedOperation = detectOperations.get(0);
        Operation startedOperation = detectOperations.get(1);
        assertOperationWithFailure(operationName, completedOperation);
        assertEquals(operationName, startedOperation.getName());
        assertEquals(StatusType.SUCCESS, startedOperation.getStatusType());
        assertNotNull(startedOperation.getStartTime());
        assertFalse(startedOperation.getEndTime().isPresent());
    }

    @Test
    public void testBeginAndSuccessOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.success();
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithSuccess(operationName, publishedOperation);
    }

    @Test
    public void testBeginAndFailOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.fail();
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithFailure(operationName, publishedOperation);
    }

    @Test
    public void testBeginAndErrorOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.error(new Exception());
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithError(operationName, publishedOperation);
    }

    @Test
    public void testSuccessOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.success();
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithSuccess(operationName, publishedOperation);
    }

    @Test
    public void testFailOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.fail();
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithFailure(operationName, publishedOperation);
    }

    @Test
    public void testErrorOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        Operation operation = operationSystem.startOperation(operationName, OperationType.PUBLIC);
        operation.error(new Exception());
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithError(operationName, publishedOperation);
    }

    private void assertOperationWithSuccess(String operationName, Operation publishedOperation) {
        assertTrue(detectIssues.isEmpty());
        assertFalse(detectOperations.isEmpty());
        assertEquals(operationName, publishedOperation.getName());
        assertEquals(StatusType.SUCCESS, publishedOperation.getStatusType());
        assertNotNull(publishedOperation.getStartTime());
        assertTrue(publishedOperation.getEndTime().isPresent());
    }

    private void assertOperationWithFailure(String operationName, Operation publishedOperation) {
        assertTrue(detectIssues.isEmpty());
        assertFalse(detectOperations.isEmpty());
        assertEquals(operationName, publishedOperation.getName());
        assertEquals(StatusType.FAILURE, publishedOperation.getStatusType());
        assertNotNull(publishedOperation.getStartTime());
        assertTrue(publishedOperation.getEndTime().isPresent());
    }

    private void assertOperationWithError(String operationName, Operation publishedOperation) {
        assertFalse(detectIssues.isEmpty());
        assertFalse(detectOperations.isEmpty());
        assertEquals(operationName, publishedOperation.getName());
        assertEquals(StatusType.FAILURE, publishedOperation.getStatusType());
        assertNotNull(publishedOperation.getStartTime());
        assertTrue(publishedOperation.getEndTime().isPresent());
        assertNotNull(publishedOperation.getException());
    }
}
