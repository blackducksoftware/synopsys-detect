/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable.detectable.executable;

import java.io.File;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

public interface ExecutableRunner {
    @NotNull
    ExecutableOutput execute(File workingDirectory, final String exeCmd, final String... args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(File workingDirectory, final String exeCmd, final List<String> args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(File workingDirectory, final File exeFile, final String... args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(File workingDirectory, final File exeFile, final List<String> args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(Executable executable) throws ExecutableRunnerException;
}
