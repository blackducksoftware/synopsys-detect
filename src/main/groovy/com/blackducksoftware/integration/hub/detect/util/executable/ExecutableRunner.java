/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.util.executable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExecutableRunner {
    private final Logger logger = LoggerFactory.getLogger(ExecutableRunner.class);

    /**
     * The output of the executable's execution will NOT be logged.
     */
    public ExecutableOutput executeQuietly(final Executable executable) throws ExecutableRunnerException {
        return execute(executable, true);
    }

    /**
     * The standard output of the executable's execution will be logged at DEBUG and any error output will be logged as
     * ERROR.
     */
    public ExecutableOutput executeLoudly(final Executable executable) throws ExecutableRunnerException {
        return execute(executable, false);
    }

    /**
     * If runQuietly == false, the standard output of the executable's execution will be logged at DEBUG and any error
     * output will be logged as
     * ERROR. Otherwise, the output of the executable's execution will NOT be logged.
     */
    public ExecutableOutput execute(final Executable executable, final boolean runQuietly) throws ExecutableRunnerException {
        logger.debug(String.format("Running executable >%s", executable.getExecutableDescription()));
        try {
            final ProcessBuilder processBuilder = executable.createProcessBuilder();
            final Process process = processBuilder.start();
            final String standardOutput = printStream(process.getInputStream(), runQuietly, false);
            final String errorOutput = printStream(process.getErrorStream(), runQuietly, true);
            final ExecutableOutput output = new ExecutableOutput(standardOutput, errorOutput);
            if (StringUtils.isNotBlank(errorOutput)) {
                throw new ExecutableRunnerException(output);
            }
            return output;
        } catch (final IOException e) {
            throw new ExecutableRunnerException(e);
        }
    }

    private String printStream(final InputStream inputStream, final boolean runQuietly, final boolean error) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
            if (!runQuietly) {
                if (error && StringUtils.isNotBlank(line)) {
                    logger.error(line);
                } else {
                    logger.debug(line);
                }
            }
        }
        return stringBuilder.toString();
    }

}
