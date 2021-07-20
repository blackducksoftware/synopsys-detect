/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step.utility;

import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.OperationType;

public class StepHelper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OperationSystem operationSystem;
    private final OperationWrapper operationWrapper;
    private final DetectToolFilter detectToolFilter;

    public StepHelper(OperationSystem operationSystem, OperationWrapper operationWrapper, DetectToolFilter detectToolFilter) {
        this.operationSystem = operationSystem;
        this.operationWrapper = operationWrapper;
        this.detectToolFilter = detectToolFilter;
    }

    public void runToolIfIncluded(DetectTool detectTool, String name, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        runToolIfIncluded(detectTool, name, null, supplier);
    }

    public void runToolIfIncluded(DetectTool detectTool, String name, @Nullable String phoneHomeKey, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        runToolIfIncluded(detectTool, name, phoneHomeKey, () -> {
            supplier.execute();
            return true;
        }, () -> {}, (e) -> {});
    }

    public <T> Optional<T> runToolIfIncluded(DetectTool detectTool, String name, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        return runToolIfIncluded(detectTool, name, null, supplier);
    }

    public <T> Optional<T> runToolIfIncluded(DetectTool detectTool, String name, @Nullable String phoneHomeKey, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        return runToolIfIncluded(detectTool, name, phoneHomeKey, supplier, () -> {}, (e) -> {});
    }

    public void runToolIfIncludedWithCallbacks(DetectTool detectTool, String name, OperationWrapper.OperationFunction supplier, Runnable successConsumer, Consumer<Exception> errorConsumer) throws DetectUserFriendlyException {
        runToolIfIncludedWithCallbacks(detectTool, name, null, supplier, successConsumer, errorConsumer);
    }

    public void runToolIfIncludedWithCallbacks(DetectTool detectTool, String name, @Nullable String phoneHomeKey, OperationWrapper.OperationFunction supplier, Runnable successConsumer, Consumer<Exception> errorConsumer)
        throws DetectUserFriendlyException {
        runToolIfIncluded(detectTool, name, phoneHomeKey, () -> {
            supplier.execute();
            return true;
        }, successConsumer, errorConsumer);
    }

    private <T> Optional<T> runToolIfIncluded(DetectTool detectTool, String name, @Nullable String phoneHomeKey, OperationWrapper.OperationSupplier<T> supplier, Runnable successConsumer, Consumer<Exception> errorConsumer)
        throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(detectTool)) {
            logger.info("Will include the " + name + " tool.");
            Operation operation = operationSystem.startOperation(name + " Tool", OperationType.INTERNAL, phoneHomeKey);
            T value = operationWrapper.namedWithCallbacks(name, operation, supplier, successConsumer, errorConsumer);
            logger.info(name + " actions finished.");
            return Optional.ofNullable(value);
        } else {
            logger.info(name + " tool will not be run.");
            return Optional.empty();
        }
    }

    public void runAsGroup(String name, OperationType type, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        runAsGroup(name, type, () -> {
            supplier.execute();
            return true;
        });
    }

    public <T> T runAsGroup(String name, OperationType type, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, type);
        return operationWrapper.named(name, operation, supplier);
    }
}
