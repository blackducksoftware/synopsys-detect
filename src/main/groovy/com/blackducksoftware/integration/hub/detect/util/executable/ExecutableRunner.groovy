/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.util.executable

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration

import groovy.transform.TypeChecked

@Component
@TypeChecked
public class ExecutableRunner {
    private final Logger logger = LoggerFactory.getLogger(ExecutableRunner.class)

    @Autowired
    DetectConfiguration detectConfiguration

    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        logger.info("Running executable >${executable.getMaskedExecutableDescription()}")
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder()
            final Process process = processBuilder.start()

            ExecutableStreamThread standardOutputThread = new ExecutableStreamThread(process.getInputStream(), logger);
            standardOutputThread.start();

            ExecutableStreamThread errorOutputThread = new ExecutableStreamThread(process.getErrorStream(), logger);
            errorOutputThread.start();

            int returnCode = process.waitFor();

            standardOutputThread.join();
            errorOutputThread.join();

            final String standardOutput = standardOutputThread.executableOutput.trim()
            final String errorOutput = errorOutputThread.executableOutput.trim()

            final ExecutableOutput output = new ExecutableOutput(standardOutput, errorOutput)
            return output
        } catch (final Exception e) {
            throw new ExecutableRunnerException(e)
        }
    }

    public void executeToFile(final Executable executable, final File standardOutput, final File errorOutput) throws ExecutableRunnerException {
        logger.debug("Running executable >${executable.getMaskedExecutableDescription()}")
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder().redirectOutput(standardOutput).redirectError(errorOutput)
            final Process process = processBuilder.start()
            process.waitFor()
        } catch (final Exception e) {
            throw new ExecutableRunnerException(e)
        }
    }

    public void runExeToFile(final String exePath, final File outputFile, final File errorFile, final String... args) {
        def exe = new Executable(new File(detectConfiguration.getSourcePath()), exePath, args.toList())
        executeToFile(exe, outputFile, errorFile)
    }

    public ExecutableOutput runExe(final String exePath, final String... args) {
        def exe = new Executable(detectConfiguration.sourceDirectory, exePath, args.toList())
        execute(exe)
    }
}
