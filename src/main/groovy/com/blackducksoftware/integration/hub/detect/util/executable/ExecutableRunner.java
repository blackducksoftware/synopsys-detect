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
package com.blackducksoftware.integration.hub.detect.util.executable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExecutableRunner {
    private final Logger logger = LoggerFactory.getLogger(ExecutableRunner.class);

    public ExecutableOutput execute(final Executable executable) throws ExecutableRunnerException {
        logger.info(String.format("Running executable >%s", executable.getExecutableDescription()));
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder();
            final Process process = processBuilder.start();
            final String standardOutput = printStream(process.getInputStream());
            final String errorOutput = printStream(process.getErrorStream());
            final ExecutableOutput output = new ExecutableOutput(standardOutput, errorOutput);
            return output;
        } catch (final Exception e) {
            throw new ExecutableRunnerException(e);
        }
    }

    public void executeToFile(final Executable executable, final File standardOutput, final File errorOutput) throws ExecutableRunnerException {
        logger.debug(String.format("Running executable >%s", executable.getExecutableDescription()));
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder().redirectOutput(standardOutput).redirectError(errorOutput);
            final Process process = processBuilder.start();
            process.waitFor();
        } catch (final Exception e) {
            throw new ExecutableRunnerException(e);
        }
    }

    private String printStream(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
            logger.info(line);
        }
        return stringBuilder.toString();
    }

}
