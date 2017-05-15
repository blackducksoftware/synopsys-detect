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
package com.blackducksoftware.integration.hub.packman.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class CommandRunner {
    private final Logger logger;

    private final FileFinder fileFinder;

    private final File workingDirectory;

    private final Map<String, String> alternativeFileMap;

    private final String providedPath;

    public CommandRunner(final Logger logger, final FileFinder fileFinder, final File workingDirectory,
            final Map<String, String> alternativeFileMap) {
        this.logger = logger;
        this.fileFinder = fileFinder;
        this.workingDirectory = workingDirectory;
        this.alternativeFileMap = alternativeFileMap;
        this.providedPath = null;
    }

    public CommandRunner(final Logger logger, final FileFinder fileFinder, final File workingDirectory,
            final Map<String, String> alternativeFileMap,
            final String path) {
        this.logger = logger;
        this.fileFinder = fileFinder;
        this.workingDirectory = workingDirectory;
        this.alternativeFileMap = alternativeFileMap;
        this.providedPath = path;
    }

    public String executeQuietly(final Command command) {
        return execute(command, true);
    }

    public String execute(final Command command) {
        return execute(command, false);
    }

    public String execute(final Command command, final boolean runQuietly) {
        String executable = command.getExecutableName(alternativeFileMap);
        if (alternativeFileMap != null && alternativeFileMap.containsKey(executable)) {
            executable = alternativeFileMap.get(executable);
        }

        if (providedPath != null) {
            executable = fileFinder.findExecutablePath(executable, providedPath);
        } else {
            executable = fileFinder.findExecutablePath(executable);
        }

        if (executable == null) {
            throw new RuntimeException(String.format("Could not find executable >%s", command.getExecutableName(null)));
        }

        return executeExactly(runQuietly, executable, command.getArgs());
    }

    private String executeExactly(final boolean runQuietly, final String executable, final String... args) {
        // We have to wrap Arrays.asList() because the supplied list does not support adding at an index
        final List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (executable != null) {
            arguments.add(0, executable);
        }

        final ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.directory(workingDirectory);

        logger.debug(String.format("Running command >%s", StringUtils.join(" ", arguments)));
        try {
            Process process;
            process = processBuilder.start();
            final StringBuilder output = new StringBuilder();
            final String infoOutput = printStream(process.getInputStream(), runQuietly, false);
            final String errorOutput = printStream(process.getErrorStream(), runQuietly, true);
            output.append(infoOutput);
            output.append("\n");
            output.append(errorOutput);
            return output.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String printStream(final InputStream inputStream, final boolean runQuietly, final boolean error) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
                if (!runQuietly || logger.isTraceEnabled()) {
                    if (error && StringUtils.isNotBlank(line)) {
                        logger.error(line);
                    } else {
                        logger.info(line);
                    }
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

}
