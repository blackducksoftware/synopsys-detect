/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.util;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ToolVersionLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;

    public ToolVersionLogger(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public void log(ExecutableTarget executableTarget, File projectDir) {
        log(executableTarget, projectDir, "--version");
    }

    public void log(ExecutableTarget executableTarget, File projectDir, String versionArgument) {
        log(() -> executableRunner.execute(ExecutableUtils.createFromTarget(projectDir, executableTarget, versionArgument)));
    }

    @FunctionalInterface
    public interface ToolExecutor {
        public void execute() throws Exception;
    }

    public void log(ToolExecutor showToolVersionExecutor) {
        if (logger.isDebugEnabled()) {
            try {
                showToolVersionExecutor.execute(); // executors log output at debug
            } catch (Exception e) {
                logger.debug("Unable to log tool version: {}", e.getMessage());
            }
        }
    }
}
