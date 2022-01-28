package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BazelCommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final File workspaceDir;
    private final ExecutableTarget bazelExe;

    public BazelCommandExecutor(DetectableExecutableRunner executableRunner, File workspaceDir, ExecutableTarget bazelExe) {
        this.executableRunner = executableRunner;
        this.workspaceDir = workspaceDir;
        this.bazelExe = bazelExe;
    }

    public Optional<String> executeToString(List<String> args) throws DetectableException {
        ExecutableOutput executableOutput = execute(args);
        String cmdStdErr = executableOutput.getErrorOutput();
        if (cmdStdErr != null && cmdStdErr.contains("ERROR")) {
            logger.warn(String.format("Bazel error: %s", cmdStdErr.trim()));
        }
        String cmdStdOut = executableOutput.getStandardOutput();
        if ((StringUtils.isBlank(cmdStdOut))) {
            logger.debug("bazel command produced no output");
            return Optional.empty();
        }
        return Optional.of(cmdStdOut);
    }

    @NotNull
    private ExecutableOutput execute(List<String> args) throws DetectableException {
        logger.debug(String.format("Executing bazel with args: %s", args));
        ExecutableOutput targetDependenciesQueryResults;
        try {
            targetDependenciesQueryResults = executableRunner.execute(ExecutableUtils.createFromTarget(workspaceDir, bazelExe, args));
        } catch (ExecutableRunnerException e) {
            String msg = String.format("Error executing %s with args: %s", bazelExe, args);
            logger.debug(msg);
            throw new DetectableException(msg, e);
        }
        int targetDependenciesQueryReturnCode = targetDependenciesQueryResults.getReturnCode();
        if (targetDependenciesQueryReturnCode != 0) {
            String msg = String.format("Error executing bazel with args: %s: Return code: %d; stderr: %s", args,
                targetDependenciesQueryReturnCode,
                targetDependenciesQueryResults.getErrorOutput());
            logger.debug(msg);
            throw new DetectableException(msg);
        }
        logger.debug(String.format("bazel command return code: %d", targetDependenciesQueryReturnCode));
        return targetDependenciesQueryResults;
    }
}
