/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class ProcessBuilderExecutableRunner implements ExecutableRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Consumer<String> outputConsumer;
    private final Consumer<String> traceConsumer;

    public ProcessBuilderExecutableRunner() {
        this.outputConsumer = logger::debug;
        this.traceConsumer = logger::trace;
    }

    public ProcessBuilderExecutableRunner(final Consumer<String> outputConsumer, final Consumer<String> traceConsumer) {
        this.outputConsumer = outputConsumer;
        this.traceConsumer = traceConsumer;
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final String exeCmd, final String... args) throws ExecutableRunnerException {
        return execute(new Executable(workingDirectory, new HashMap<>(), exeCmd, Arrays.asList(args)));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final String exeCmd, final List<String> args) throws ExecutableRunnerException {
        return execute(new Executable(workingDirectory, new HashMap<>(), exeCmd, args));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final File exeFile, final String... args) throws ExecutableRunnerException {
        return execute(new Executable(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), Arrays.asList(args)));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final File workingDirectory, final File exeFile, final List<String> args) throws ExecutableRunnerException {
        return execute(new Executable(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), args));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        logger.info(String.format("Running executable >%s", executable.getMaskedExecutableDescription()));
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder();
            final Process process = processBuilder.start();

            try (final InputStream standardOutputStream = process.getInputStream(); final InputStream standardErrorStream = process.getErrorStream()) {
                final ExecutableStreamThread standardOutputThread = new ExecutableStreamThread(standardOutputStream, outputConsumer, traceConsumer);
                standardOutputThread.start();

                final ExecutableStreamThread errorOutputThread = new ExecutableStreamThread(standardErrorStream, outputConsumer, traceConsumer);
                errorOutputThread.start();

                final int returnCode = process.waitFor();
                logger.info("Executable finished: " + returnCode);

                standardOutputThread.join();
                errorOutputThread.join();

                final String standardOutput = standardOutputThread.getExecutableOutput().trim();
                final String errorOutput = errorOutputThread.getExecutableOutput().trim();

                final ExecutableOutput output = new ExecutableOutput(executable.getMaskedExecutableDescription(), returnCode, standardOutput, errorOutput);
                return output;
            }
        } catch (final IOException | InterruptedException e) {
            throw new ExecutableRunnerException(executable.getMaskedExecutableDescription(), e);
        }
    }

    @Override
    public @NotNull Executable translate(final File workingDirectory, final File exeFile, final List<String> args) {
        return new Executable(workingDirectory, new HashMap<>(), exeFile.getAbsolutePath(), args);
    }
}