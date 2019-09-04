package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GradleTaskChecker {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final ExecutableRunner executableRunner;

    public GradleTaskChecker(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public Optional<String> getGoGradleTask(final File workingDirectory, final File gradleExe) {
        try {
            final ExecutableOutput executableOutput = executableRunner.execute(workingDirectory, gradleExe, "tasks");
            return findFirstTask(executableOutput, "goLock", "lock");
        } catch (final ExecutableRunnerException e) {
            logger.debug("Failed parse gradle tasks.", e);
            return Optional.empty();
        }
    }

    private Optional<String> findFirstTask(final ExecutableOutput executableOutput, final String... tasks) {
        boolean inGoGradleTasksSection = false;
        String foundTask = null;
        for (final String line : executableOutput.getStandardOutputAsList()) {
            if (StringUtils.isBlank(line)) {
                inGoGradleTasksSection = false;
                continue;
            }

            if (line.trim().startsWith("Gogradle tasks")) {
                inGoGradleTasksSection = true;
                continue;
            }

            if (inGoGradleTasksSection) {
                for (final String task : tasks) {
                    if (line.trim().startsWith(task)) {
                        foundTask = task;
                        break;
                    }
                }
            }
        }

        return Optional.ofNullable(foundTask);
    }
}
