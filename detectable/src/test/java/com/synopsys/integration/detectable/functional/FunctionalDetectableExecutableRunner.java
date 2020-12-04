/**
 * detectable
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
package com.synopsys.integration.detectable.functional;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class FunctionalDetectableExecutableRunner implements DetectableExecutableRunner {
    private final Map<FunctionalExecutable, ExecutableOutput> executableExecutableOutputMap = new HashMap<>();

    public void addExecutableOutput(@NotNull final Executable executable, @NotNull final ExecutableOutput executableOutput) {
        executableExecutableOutputMap.put(new FunctionalExecutable(executable), executableOutput);
    }

    @Override
    public @NotNull ExecutableOutput execute(final File workingDirectory, final List<String> command) throws ExecutableRunnerException {
        return execute(new Executable(workingDirectory, new HashMap<>(), command));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final String exeCmd, @NotNull final String... args) {
        return execute(workingDirectory, new File(exeCmd), args);
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final String exeCmd, @NotNull final List<String> args) {
        return execute(workingDirectory, new File(exeCmd), args);
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final File exeFile, @NotNull final String... args) {
        return execute(workingDirectory, exeFile, Arrays.asList(args));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final File exeFile, @NotNull final List<String> args) {

        final List<String> command = new ArrayList<>();
        command.add(exeFile.getPath());
        command.addAll(args);

        return execute(new Executable(workingDirectory, new HashMap<>(), command));
    }

    @Override
    public @NotNull ExecutableOutput executeSecretly(final Executable executable) throws ExecutableRunnerException {
        return execute(executable);//Functional tests don't care about 'secret' executions (where secret means the output might contain something secret like credentials).
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull final Executable executable) {

        final ExecutableOutput executableOutput = executableExecutableOutputMap.get(new FunctionalExecutable(executable));
        if (executableOutput == null) {
            final StringBuilder errorMessage = new StringBuilder("Missing mocked executable output for:")
                                                   .append(System.lineSeparator())
                                                   .append(executable.getExecutableDescription())
                                                   .append(System.lineSeparator());

            errorMessage.append("Executable Output Map Contents:").append(System.lineSeparator());
            executableExecutableOutputMap.forEach((key, value) -> {
                errorMessage.append("--------------------------------").append(System.lineSeparator());

                errorMessage.append("Key: ")
                    .append(System.lineSeparator())
                    .append(key.getReferencedExecutable().getExecutableDescription())
                    .append(System.lineSeparator());
                errorMessage.append("Standard Output: ")
                    .append(System.lineSeparator())
                    .append(value.getStandardOutput())
                    .append(System.lineSeparator());
                errorMessage.append("Error Output: ")
                    .append(System.lineSeparator())
                    .append(value.getErrorOutput())
                    .append(System.lineSeparator());
                errorMessage.append("Return Code: ")
                    .append(System.lineSeparator())
                    .append(value.getReturnCode())
                    .append(System.lineSeparator());
                errorMessage.append(System.lineSeparator());
            });

            throw new RuntimeException(errorMessage.toString());
        }

        return executableOutput;
    }

    @Override
    public @NotNull ExecutableOutput executeSuccessfully(final Executable executable) throws ExecutableFailedException {
        ExecutableOutput output = execute(executable);
        if (output.getReturnCode() != 0)
            throw new ExecutableFailedException(executable, output);

        return output;
    }
}
