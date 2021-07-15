/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step.utility;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
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

    public void namedPublic(String name, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        namedPublic(name, null, supplier);
    }

    public void namedPublic(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, OperationType.PUBLIC, phoneHomeKey);
        operationWrapper.named(name, operation, supplier);
    }

    public <T> T namedPublic(String name, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        return namedPublic(name, null, supplier);
    }

    public <T> T namedPublic(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, OperationType.PUBLIC, phoneHomeKey);
        return operationWrapper.named(name, operation, supplier);
    }

    public void namedInternal(String name, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        namedInternal(name, null, supplier);
    }

    public void namedInternal(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, OperationType.INTERNAL, phoneHomeKey);
        operationWrapper.named(name, operation, supplier);
    }

    public <T> T namedInternal(String name, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        return namedInternal(name, null, supplier);
    }

    public <T> T namedInternal(String name, @Nullable String phoneHomeKey, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, OperationType.INTERNAL, phoneHomeKey);
        return operationWrapper.named(name, operation, supplier);
    }
}
