package com.synopsys.integration.detect.lifecycle.run.step.utility;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.OperationType;

//Essentially an adapter for 'running an operation' and 'reporting the operation' in one step. Whether or not this is desired is TBD.
public class OperationAuditLog {
    private final OperationWrapper operationWrapper;
    private final OperationSystem operationSystem;

    public OperationAuditLog(OperationWrapper operationWrapper, OperationSystem operationSystem) {
        this.operationWrapper = operationWrapper;
        this.operationSystem = operationSystem;
    }

    public void namedPublic(String name, OperationWrapper.OperationFunction supplier) throws OperationException {
        namedPublic(name, null, supplier);
    }

    public void namedPublic(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationFunction supplier) throws OperationException {
        Operation operation = operationSystem.startOperation(name, OperationType.PUBLIC, phoneHomeKey);
        operationWrapper.wrapped(operation, supplier);
    }

    public <T> T namedPublic(String name, OperationWrapper.OperationSupplier<T> supplier) throws OperationException {
        return namedPublic(name, null, supplier);
    }

    public <T> T namedPublic(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationSupplier<T> supplier) throws OperationException {
        Operation operation = operationSystem.startOperation(name, OperationType.PUBLIC, phoneHomeKey);
        return operationWrapper.wrapped(operation, supplier);
    }

    public void namedInternal(String name, OperationWrapper.OperationFunction supplier) throws OperationException {
        namedInternal(name, null, supplier);
    }

    public void namedInternal(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationFunction supplier) throws OperationException {
        Operation operation = operationSystem.startOperation(name, OperationType.INTERNAL, phoneHomeKey);
        operationWrapper.wrapped(operation, supplier);
    }

    public <T> T namedInternal(String name, OperationWrapper.OperationSupplier<T> supplier) throws OperationException {
        return namedInternal(name, null, supplier);
    }

    public <T> T namedInternal(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationSupplier<T> supplier) throws OperationException {
        Operation operation = operationSystem.startOperation(name, OperationType.INTERNAL, phoneHomeKey);
        return operationWrapper.wrapped(operation, supplier);
    }
}
