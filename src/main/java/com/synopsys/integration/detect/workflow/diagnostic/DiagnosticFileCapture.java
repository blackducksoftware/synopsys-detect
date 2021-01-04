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

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DiagnosticFileCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int files = 0;
    private final File fileDirectory;
    private final Map<Integer, String> fileNames = new HashMap<>();

    public DiagnosticFileCapture(final File fileDirectory, final EventSystem eventSystem) {
        this.fileDirectory = fileDirectory;
        eventSystem.registerListener(Event.CustomerFileOfInterest, this::fileFound);
    }

    private void fileFound(final File foundFile) {
        final File savedFile = new File(fileDirectory, "FILE-" + files + "-" + foundFile.getName());
        fileNames.put(files, foundFile.toString());

        try {
            FileUtils.copyFile(foundFile, savedFile);
            logger.info("Saved file to diagnostics zip: " + foundFile.toString());
        } catch (final IOException e) {
            logger.error("Failed to copy file of interest.", e);
        }
        files++;
    }

    public void finish() {
        if (fileNames.size() <= 0)
            return;

        final AtomicReference<String> executableMap = new AtomicReference<>("");
        fileNames.forEach((key, value) -> {
            executableMap.set(executableMap.get() + key + ": " + value + System.lineSeparator());
        });

        final File mapFile = new File(fileDirectory, "FILE-MAP.txt");
        try {
            FileUtils.writeStringToFile(mapFile, executableMap.get(), Charset.defaultCharset());
        } catch (final IOException e) {
            logger.error("Failed to write executable map.", e);
        }
    }
}
