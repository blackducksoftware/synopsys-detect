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

import org.slf4j.Logger;

public class ExecutableStreamThread extends Thread {
    private final BufferedReader bufferedReader;
    private final StringBuilder stringBuilder;
    private final Logger logger;

    public String executableOutput;

    public ExecutableStreamThread(final InputStream executableStream, final Logger logger) {
        super("Executable Stream Thread");
        this.logger = logger;
        final InputStreamReader reader = new InputStreamReader(executableStream, StandardCharsets.UTF_8);
        this.bufferedReader = new BufferedReader(reader);
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public void run() {
        try {
            String line;
            final String separator = System.lineSeparator();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + separator);
                logger.info(line);
            }
        } catch (final IOException e) {
            // Ignore
            logger.trace(e.toString());
        }
        this.executableOutput = stringBuilder.toString();
    }

}
