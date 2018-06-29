/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

@Component
public class DetectFileManager {
    private final Logger logger = LoggerFactory.getLogger(DetectFileManager.class);

    private final DetectConfigWrapper detectConfigWrapper;

    private final String sharedUUID = "shared";
    private File sharedDirectory = null;
    private final Map<ExtractionId, File> outputDirectories = new HashMap<>();
    //private final Map<ExtractionContext, File> outputDirectories = new HashMap<>();

    @Autowired
    public DetectFileManager(final DetectConfigWrapper detectConfigWrapper) {
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public File getOutputDirectory(final String name, final ExtractionId extractionId) {
        if (outputDirectories.containsKey(extractionId)) {
            return outputDirectories.get(extractionId);
        } else {
            final String directoryName = name + "-" + extractionId.toUniqueString();

            final File newDirectory = new File(getExtractionFile(), directoryName);
            newDirectory.mkdir();
            outputDirectories.put(extractionId, newDirectory);
            return newDirectory;
        }
    }

    private File getExtractionFile() {
        final File newDirectory = new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_OUTPUT_PATH), "extractions");
        newDirectory.mkdir();
        return newDirectory;
    }

    public File getOutputFile(final File outputDirectory, final String name) {
        return new File(outputDirectory, name);
    }

    public File getSharedDirectory(String name) { //shared across this invocation of detect.
        if (sharedDirectory == null) {
            sharedDirectory = new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_OUTPUT_PATH), sharedUUID);
            sharedDirectory.mkdir();
        }
        File newSharedFile = new File(sharedDirectory, name);
        newSharedFile.mkdir();
        return newSharedFile;
    }

    public File getPermanentDirectory() { //shared across all invocations of detect
        final File newDirectory = new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_OUTPUT_PATH), "tools");
        newDirectory.mkdir();
        return newDirectory;
    }

    public File writeToFile(final File file, final String contents) throws IOException {
        return writeToFile(file, contents, true);
    }

    public File createSharedFile(final String directory, final String filename) {
        return new File(getSharedDirectory(directory), filename);
    }

    //This file will be immediately cleaned up and is associated to a specific context. The current implementation is to actually move it to the context's output and allow cleanup at the end of the detect run (in case of diagnostics).
    public void cleanupOutputFile(final File outputDirectory, final File file) {
        try {
            if (file.isFile()) {
                final File dest = new File(outputDirectory, file.getName());
                FileUtils.moveFile(file, dest);

            } else if (file.isDirectory()) {
                final File dest = new File(outputDirectory, file.getName());
                FileUtils.moveDirectory(file, dest);
            }
        } catch (final Exception e) {

        }

    }

    public void cleanupDirectories() {
        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_CLEANUP)) {
            for (final File file : outputDirectories.values()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (final IOException e) {
                    logger.error("Failed to cleanup: " + file.getPath());
                    e.printStackTrace();
                }
            }
        }
    }

    private File writeToFile(final File file, final String contents, final boolean overwrite) throws IOException {
        if (file == null) {
            return null;
        }
        if (overwrite && file.exists()) {
            file.delete();
        }
        if (file.exists()) {
            logger.info(String.format("%s exists and not being overwritten", file.getAbsolutePath()));
        } else {
            FileUtils.write(file, contents, StandardCharsets.UTF_8);
        }
        return file;
    }

}
