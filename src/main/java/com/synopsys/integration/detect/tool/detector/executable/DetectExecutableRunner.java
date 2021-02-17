/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
    private ProcessBuilderRunner runner;
    private ProcessBuilderRunner secretRunner;

    private DetectExecutableRunner(Logger logger, final Consumer<String> outputConsumer, final Consumer<String> traceConsumer, EventSystem eventSystem, boolean shouldLogOutput) {
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
    public @NotNull ExecutableOutput execute(final File workingDirectory, final List<String> command) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, command));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final String exeCmd, final String... args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeCmd, Arrays.asList(args)));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final String exeCmd, final List<String> args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeCmd, args));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final File exeFile, final String... args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), Arrays.asList(args)));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final File exeFile, final List<String> args) throws ExecutableRunnerException {
        return execute(Executable.create(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), args));
    }

    @NotNull
    public ExecutableOutput execute(final Executable executable, boolean outputContainsSecret) throws ExecutableRunnerException {
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
    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        return execute(executable, false);
    }

    @NotNull
    @Override
    public ExecutableOutput executeSecretly(final Executable executable) throws ExecutableRunnerException {
        return execute(executable, true);
    }

    @Override
    public @NotNull ExecutableOutput executeSuccessfully(final Executable executable) throws ExecutableFailedException {
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
