/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step.utility;

import java.util.Optional;

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

    public StepHelper(final OperationSystem operationSystem, final OperationWrapper operationWrapper, DetectToolFilter detectToolFilter) {
        this.operationSystem = operationSystem;
        this.operationWrapper = operationWrapper;
        this.detectToolFilter = detectToolFilter;
    }

    public void runToolIfIncluded(DetectTool detectTool, String name, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(detectTool)) {
            logger.info("Will include the " + name + " tool.");
            runAsGroup(name, supplier);
            logger.info(name + " actions finished.");
        } else {
            logger.info(name + " tool will not be run.");
        }
    }

    public <T> Optional<T> runToolIfIncluded(DetectTool detectTool, String name, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(detectTool)) {
            logger.info("Will include the " + name + " tool.");
            Optional<T> value = Optional.ofNullable(runAsGroup(name + " Tool", supplier));
            logger.info(name + " actions finished.");
            return value;
        } else {
            logger.info(name + " tool will not be run.");
            return Optional.empty();
        }
    }

    public void runAsGroup(String name, OperationWrapper.OperationFunction supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, OperationType.INTERNAL);
        operationWrapper.named(name, operation, supplier);
    }

    public <T> T runAsGroup(String name, OperationWrapper.OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name, OperationType.INTERNAL);
        return operationWrapper.named(name, operation, supplier);
    }
}
