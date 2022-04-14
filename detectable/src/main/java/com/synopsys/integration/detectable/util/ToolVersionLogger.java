package com.synopsys.integration.detectable.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;

//TODO: This should be phoned home. Also should be put in a report in diagnostics.
public class ToolVersionLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;

    public ToolVersionLogger(DetectableExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public void log(File projectDir, ExecutableTarget executableTarget) {
        log(projectDir, executableTarget, "--version");
    }

    public void log(File projectDir, ExecutableTarget executableTarget, String versionArgument) {
        log(() -> executableRunner.execute(ExecutableUtils.createFromTarget(projectDir, executableTarget, versionArgument)));
    }

    @FunctionalInterface
    public interface ToolExecutor {
        void execute() throws Exception;
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
