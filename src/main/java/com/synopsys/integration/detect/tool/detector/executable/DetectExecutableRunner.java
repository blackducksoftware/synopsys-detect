package com.synopsys.integration.detect.tool.detector.executable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunner;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.executable.ProcessBuilderRunner;
import com.synopsys.integration.log.Slf4jIntLogger;

public class DetectExecutableRunner implements DetectableExecutableRunner {
    private final Logger logger;
    private final EventSystem eventSystem;
    private final boolean shouldLogOutput;
    private final ProcessBuilderRunner runner;
    private final ProcessBuilderRunner secretRunner;

    private DetectExecutableRunner(Logger logger, Consumer<String> outputConsumer, Consumer<String> traceConsumer, EventSystem eventSystem, boolean shouldLogOutput) {
        this.logger = logger;
        runner = new ProcessBuilderRunner(new Slf4jIntLogger(logger), outputConsumer, traceConsumer);
        secretRunner = new ProcessBuilderRunner(new Slf4jIntLogger(logger), (line) -> {}, line -> {});
        this.eventSystem = eventSystem;
        this.shouldLogOutput = shouldLogOutput;

    }

    public static DetectExecutableRunner newDebug(EventSystem eventSystem) {
        Logger logger = LoggerFactory.getLogger(DetectExecutableRunner.class);
        return new DetectExecutableRunner(logger, logger::debug, logger::trace, eventSystem, true);
    }

    public static DetectExecutableRunner newInfo(EventSystem eventSystem) {
        Logger logger = LoggerFactory.getLogger(DetectExecutableRunner.class);
        return new DetectExecutableRunner(logger, logger::info, logger::trace, eventSystem, false);
    }

    @Override
    @NotNull
    public ExecutableOutput execute(File workingDirectory, List<String> command) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, command));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(File workingDirectory, String exeCmd, String... args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeCmd, Arrays.asList(args)));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(File workingDirectory, String exeCmd, List<String> args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeCmd, args));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(File workingDirectory, File exeFile, String... args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), Arrays.asList(args)));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(File workingDirectory, File exeFile, List<String> args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), args));
    }

    @NotNull
    public ExecutableOutput execute(Executable executable, boolean outputContainsSecret) throws ExecutableRunnerException {
        ExecutableRunner targetRunner = runner;
        if (outputContainsSecret) {
            targetRunner = secretRunner;
        }
        ExecutableOutput output = targetRunner.execute(executable);
        eventSystem.publishEvent(Event.Executable, new ExecutedExecutable(output, executable));
        boolean normallyLogOutput = output.getReturnCode() != 0 && shouldLogOutput && !logger.isDebugEnabled() && !logger.isTraceEnabled();
        if (normallyLogOutput && !outputContainsSecret) {
            if (StringUtils.isNotBlank(output.getStandardOutput())) {
                logger.info("Standard Output: ");
                logger.info(output.getStandardOutput());
            }

            if (StringUtils.isNotBlank(output.getErrorOutput())) {
                logger.info("Error Output: ");
                logger.info(output.getErrorOutput());
            }
        }
        return output;
    }

    @NotNull
    @Override
    public ExecutableOutput execute(Executable executable) throws ExecutableRunnerException {
        return execute(executable, false);
    }

    @NotNull
    @Override
    public ExecutableOutput executeSecretly(Executable executable) throws ExecutableRunnerException {
        return execute(executable, true);
    }

    @Override
    @NotNull
    public ExecutableOutput executeSuccessfully(Executable executable) throws ExecutableFailedException {
        try {
            ExecutableOutput executableOutput = execute(executable);
            if (executableOutput.getReturnCode() != 0) {
                throw new ExecutableFailedException(executable, executableOutput);
            }
            return executableOutput;
        } catch (ExecutableRunnerException e) {
            throw new ExecutableFailedException(executable, e);
        }
    }
}
