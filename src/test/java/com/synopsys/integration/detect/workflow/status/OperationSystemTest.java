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
        eventSystem.registerListener(Event.DetectOperation, this::addOperation);
        eventSystem.registerListener(Event.Issue, this::addIssue);
    }

    private void addIssue(DetectIssue issue) {
        detectIssues.add(issue);
    }

    private void addOperation(Operation operation) {
        detectOperations.add(operation);
    }

    @Test
    public void testStartOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        operationSystem.beginOperation(operationName);
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
        operationSystem.completeWithFailure(operationName);
        operationSystem.beginOperation(operationName);
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
        operationSystem.beginOperation(operationName);
        operationSystem.completeWithSuccess(operationName);
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithSuccess(operationName, publishedOperation);
    }

    @Test
    public void testBeginAndFailOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        operationSystem.beginOperation(operationName);
        operationSystem.completeWithFailure(operationName);
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithFailure(operationName, publishedOperation);
    }

    @Test
    public void testBeginAndErrorOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        operationSystem.beginOperation(operationName);
        operationSystem.completeWithError(operationName, "Unit test error message");
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithError(operationName, publishedOperation);
    }

    @Test
    public void testSuccessOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        operationSystem.completeWithSuccess(operationName);
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithSuccess(operationName, publishedOperation);
    }

    @Test
    public void testFailOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        operationSystem.completeWithFailure(operationName);
        operationSystem.publishOperations();
        Operation publishedOperation = detectOperations.get(0);
        assertOperationWithFailure(operationName, publishedOperation);
    }

    @Test
    public void testErrorOperation() {
        OperationSystem operationSystem = new OperationSystem(statusEventPublisher);
        String operationName = "myOperation";
        operationSystem.completeWithError(operationName, "Unit test error message");
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
        assertTrue(publishedOperation.getErrorMessages().length > 0);
    }
}
