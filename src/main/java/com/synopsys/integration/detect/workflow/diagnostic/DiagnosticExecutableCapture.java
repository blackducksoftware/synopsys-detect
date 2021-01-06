/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.diagnostic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.executable.ExecutedExecutable;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DiagnosticExecutableCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int executables = 0;
    private final File executableDirectory;
    private final Map<Integer, String> indexToCommand = new HashMap<>();

    public DiagnosticExecutableCapture(final File executableDirectory, final EventSystem eventSystem) {
        this.executableDirectory = executableDirectory;
        eventSystem.registerListener(Event.Executable, this::executableFinished);
    }

    private void executableFinished(final ExecutedExecutable executed) {
        final File errorOut = new File(executableDirectory, "EXE-" + executables + "-ERR.xout");
        final File standardOut = new File(executableDirectory, "EXE-" + executables + "-STD.xout");
        indexToCommand.put(executables, executed.getExecutable().getExecutableDescription());

        try {
            FileUtils.writeStringToFile(errorOut, executed.getOutput().getErrorOutput(), Charset.defaultCharset());
            FileUtils.writeStringToFile(standardOut, executed.getOutput().getStandardOutput(), Charset.defaultCharset());
        } catch (final IOException e) {
            logger.error("Failed to capture executable output.", e);
        }
        executables++;
    }

    public void finish() {
        if (indexToCommand.size() <= 0) {
            return;
        }

        final AtomicReference<String> executableMap = new AtomicReference<>("");
        indexToCommand.forEach((key, value) -> {
            executableMap.set(executableMap.get() + key + ": " + value + System.lineSeparator());
        });

        final File mapFile = new File(executableDirectory, "EXE-MAP.txt");
        try {
            FileUtils.writeStringToFile(mapFile, executableMap.get(), Charset.defaultCharset());
        } catch (final IOException e) {
            logger.error("Failed to write executable map.", e);
        }
    }
}
