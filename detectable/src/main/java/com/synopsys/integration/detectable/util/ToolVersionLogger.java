/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.util;

import com.synopsys.integration.executable.ExecutableOutput;
import org.slf4j.Logger;

public class ToolVersionLogger {

    @FunctionalInterface
    public interface ExceptionThrowingSupplier<T> {
        public T get() throws Exception;
    }

    public void logOutputSafelyIfDebug(Logger logger, ExceptionThrowingSupplier<ExecutableOutput> executor, String toolName) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug(executor.get().getStandardOutput());
            } catch (Exception e) {
                logger.debug("Unable to log {} version: {}", toolName, e.getMessage());
            }
        }
    }
}
