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
import org.slf4j.LoggerFactory;

public class ToolVersionLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FunctionalInterface
    public interface ToolExecutor {
        public void execute() throws Exception;
    }

    public void logOutputSafelyIfDebug(ToolExecutor showToolVersionExecutor, String toolName) {
        if (logger.isDebugEnabled()) {
            try {
                showToolVersionExecutor.execute(); // executors log output at debug
            } catch (Exception e) {
                logger.debug("Unable to log {} version: {}", toolName, e.getMessage());
            }
        }
    }
}
