/*
 * detectable
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
package com.synopsys.integration.detectable.detectable.executable;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

//This is essentially temporary as we migrate to Integration commons executable runner and allow detectables to throw exceptions.
public interface DetectableExecutableRunner {
    @NotNull
    ExecutableOutput execute(final File workingDirectory, List<String> command) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final String exeCmd, final String... args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final String exeCmd, final List<String> args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final File exeFile, final String... args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final File exeFile, final List<String> args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(Executable executable) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput executeSecretly(Executable executable) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput executeSuccessfully(Executable executable) throws ExecutableFailedException; //Returns output if and only if executable return code was zero, otherwise throws.
}
