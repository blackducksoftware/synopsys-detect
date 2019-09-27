package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.exception.IntegrationException;

public class BazelCommandExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;
    private final File workspaceDir;
    private final File bazelExe;

    public BazelCommandExecutor(final ExecutableRunner executableRunner, final File workspaceDir, final File bazelExe) {
        this.executableRunner = executableRunner;
        this.workspaceDir = workspaceDir;
        this.bazelExe = bazelExe;
    }

    public Optional<String> executeToString(final List<String> args) throws IntegrationException {
        final ExecutableOutput targetDependenciesQueryResults = execute(args);
        final String targetDependenciesQueryOutput = targetDependenciesQueryResults.getStandardOutput();
        logger.debug(String.format("bazel output: %s", targetDependenciesQueryOutput));
        if ((StringUtils.isBlank(targetDependenciesQueryOutput))) {
            logger.debug("bazel command produced no output");
            return Optional.empty();
        }
        return Optional.of(targetDependenciesQueryOutput);
    }

    @NotNull
    private ExecutableOutput execute(final List<String> args) throws IntegrationException {
        logger.debug(String.format("Executing bazel with args: %s", args));
        final ExecutableOutput targetDependenciesQueryResults;
        try {
            targetDependenciesQueryResults = executableRunner.execute(workspaceDir, bazelExe, args);
        } catch (ExecutableRunnerException e) {
            final String msg = String.format("Error executing %s with args: %s", bazelExe, args);
            logger.debug(msg);
            throw new IntegrationException(msg, e);
        }
        final int targetDependenciesQueryReturnCode = targetDependenciesQueryResults.getReturnCode();
        if (targetDependenciesQueryReturnCode != 0) {
            String msg = String.format("Error executing bazel with args: %s: Return code: %d; stderr: %s", args,
                targetDependenciesQueryReturnCode,
                targetDependenciesQueryResults.getErrorOutput());
            logger.debug(msg);
            throw new IntegrationException(msg);
        }
        logger.debug(String.format("bazel command return code: %d", targetDependenciesQueryReturnCode));
        return targetDependenciesQueryResults;
    }
}
