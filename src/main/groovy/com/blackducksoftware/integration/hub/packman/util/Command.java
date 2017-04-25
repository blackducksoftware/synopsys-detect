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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder;

public class Command {

    private final Logger logger;

    private final ExecutableFinder executableFinder;

    private final File workingDirectory;

    public boolean runQuietly = false;

    public Command(final Logger logger, final ExecutableFinder executableFinder, final File workingDirectory) {
        this.logger = logger;
        this.executableFinder = executableFinder;
        this.workingDirectory = workingDirectory;
    }

    public String execute(final String executable, final String... args) {
        String executablePath = executableFinder.findExecutable(executable);
        if (executablePath == null) {
            executablePath = executable;
        }
        return executeExactly(executablePath, args);
    }

    public String execute(final File path, final String executable, final String... args) {
        String executablePath = executableFinder.findExecutable(executable, path.getAbsolutePath());
        if (executablePath == null) {
            executablePath = executable;
        }
        return executeExactly(executablePath, args);
    }

    public String executeExactly(final String executable, final String... args) {
        final List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (executable != null) {
            arguments.add(0, executable);
        }

        final ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.directory(workingDirectory);

        logger.debug(String.format("Running command >%s", String.join(" ", arguments)));
        try {
            Process process;
            process = processBuilder.start();
            // process.waitFor();
            final StringBuilder output = new StringBuilder();
            final String infoOutput = printStream(logger, process.getInputStream(), runQuietly, false);
            final String errorOutput = printStream(logger, process.getErrorStream(), runQuietly, true);
            output.append(infoOutput);
            output.append("\n");
            output.append(errorOutput);
            return output.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String printStream(final Logger logger, final InputStream inputStream, final boolean runQuietly, final boolean error) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
                if (!runQuietly) {
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
