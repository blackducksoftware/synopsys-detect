/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.util.executable;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

public class ExecutableRunner {
    private final Logger logger = LoggerFactory.getLogger(ExecutableRunner.class);

    public ExecutableOutput execute(File workingDirectory, final String exePath, final String... args) throws ExecutableRunnerException {
        final Executable exe = new Executable(workingDirectory, exePath, Arrays.asList(args));
        return execute(exe);
    }

    public ExecutableOutput executeQuietly(File workingDirectory, final String exePath, final String... args) throws ExecutableRunnerException {
        final Executable exe = new Executable(workingDirectory, exePath, Arrays.asList(args));
        return executeQuietly(exe);
    }

    public ExecutableOutput execute(File workingDirectory, final String exePath, final List<String> args) throws ExecutableRunnerException {
        final Executable exe = new Executable(workingDirectory, exePath, args);
        return execute(exe);
    }

    public ExecutableOutput executeQuietly(File workingDirectory, final String exePath, final List<String> args) throws ExecutableRunnerException {
        final Executable exe = new Executable(workingDirectory, exePath, args);
        return executeQuietly(exe);
    }

    public ExecutableOutput executeFromDirQuietly(final File workingDir, final String exePath, final List<String> args) throws ExecutableRunnerException {
        final Executable exe = new Executable(workingDir, exePath, args);
        return executeQuietly(exe);
    }

    public ExecutableOutput execute(File workingDirectory, final File exePath, final String... args) throws ExecutableRunnerException {
        final Executable exe = new Executable(workingDirectory, exePath, Arrays.asList(args));
        return execute(exe);
    }

    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        return runExecutable(executable, logger::info, logger::trace);
    }

    public ExecutableOutput executeQuietly(final Executable executable) throws ExecutableRunnerException {
        return runExecutable(executable, logger::debug, logger::trace);
    }

    public ExecutableOutput runExecutable(final Executable executable, final Consumer<String> standardLoggingMethod, final Consumer<String> traceLoggingMethod) throws ExecutableRunnerException {
        standardLoggingMethod.accept(String.format("Running executable >%s", executable.getMaskedExecutableDescription()));
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder();
            final Process process = processBuilder.start();

            try (InputStream standardOutputStream = process.getInputStream(); InputStream standardErrorStream = process.getErrorStream()) {
                final ExecutableStreamThread standardOutputThread = new ExecutableStreamThread(standardOutputStream, standardLoggingMethod, traceLoggingMethod);
                standardOutputThread.start();

                final ExecutableStreamThread errorOutputThread = new ExecutableStreamThread(standardErrorStream, standardLoggingMethod, traceLoggingMethod);
                errorOutputThread.start();

                final int returnCode = process.waitFor();
                standardLoggingMethod.accept("Executable finished: " + returnCode);

                standardOutputThread.join();
                errorOutputThread.join();

                final String standardOutput = standardOutputThread.getExecutableOutput().trim();
                final String errorOutput = errorOutputThread.getExecutableOutput().trim();

                final ExecutableOutput output = new ExecutableOutput(returnCode, standardOutput, errorOutput);
                return output;
            }
        } catch (final Exception e) {
            throw new ExecutableRunnerException(e);
        }
    }

}
