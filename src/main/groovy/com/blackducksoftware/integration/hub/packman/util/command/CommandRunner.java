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
package com.blackducksoftware.integration.hub.packman.util.command;

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
public class CommandRunner {
    private final Logger logger = LoggerFactory.getLogger(CommandRunner.class);

    /**
     * The output of the command's execution will NOT be logged.
     */
    public CommandOutput executeQuietly(final Command command) throws CommandRunnerException {
        return execute(command, true);
    }

    /**
     * The standard output of the command's execution will be logged at DEBUG and any error output will be logged as
     * ERROR.
     */
    public CommandOutput executeLoudly(final Command command) throws CommandRunnerException {
        return execute(command, false);
    }

    /**
     * If runQuietly == false, the standard output of the command's execution will be logged at DEBUG and any error
     * output will be logged as
     * ERROR. Otherwise, the output of the command's execution will NOT be logged.
     */
    public CommandOutput execute(final Command command, final boolean runQuietly) throws CommandRunnerException {
        logger.debug(String.format("Running command >%s", command.getCommandDescription()));
        try {
            final ProcessBuilder processBuilder = command.createProcessBuilder();
            final Process process = processBuilder.start();
            final String standardOutput = printStream(process.getInputStream(), runQuietly, false);
            final String errorOutput = printStream(process.getErrorStream(), runQuietly, true);
            final CommandOutput output = new CommandOutput(standardOutput, errorOutput);
            if (StringUtils.isNotBlank(errorOutput)) {
                throw new CommandRunnerException(output);
            }
            return output;
        } catch (final IOException e) {
            throw new CommandRunnerException(e);
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
