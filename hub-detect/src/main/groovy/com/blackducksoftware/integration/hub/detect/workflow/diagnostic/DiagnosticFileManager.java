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
package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;

public class DiagnosticFileManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private File reportDirectory;
    private File relevantDirectory;
    private File extractionDirectory;
    private File logDirectory;

    private final List<File> trackedDirectories = new ArrayList<>();

    public File getReportDirectory() {
        return reportDirectory;
    }

    public File getRelevantDirectory() {
        return relevantDirectory;
    }

    public File getExtractionDirectory() {
        return extractionDirectory;
    }

    public File getLogDirectory() {
        return logDirectory;
    }

    public void init(final File outputDirectory, final File bdioDirectory, final String runId) {
        reportDirectory = new File(new File(outputDirectory, "reports"), runId);
        relevantDirectory = new File(new File(outputDirectory, "relevant"), runId);
        extractionDirectory = new File(new File(outputDirectory, "extractions"), runId);
        logDirectory = new File(new File(outputDirectory, "logs"), runId);

        trackedDirectories.add(bdioDirectory);
        trackedDirectories.add(reportDirectory);
        trackedDirectories.add(relevantDirectory);
        trackedDirectories.add(extractionDirectory);
        trackedDirectories.add(logDirectory);

        for (final File file : trackedDirectories) {
            logger.info("Creating diagnostics directory: " + file.getPath());
            file.mkdirs();
        }
    }

    public void cleanup() {
        try {
            for (final File file : trackedDirectories) {
                logger.info("Cleaning diagnostics directory: " + file.getPath());
                FileUtils.deleteDirectory(file);
            }
        } catch (final IOException e) {
            logger.error("Failed to cleanup.", e);
        }
    }

    public List<File> getAllDirectories() {
        return trackedDirectories;
    }

    public void registerFileOfInterest(final ExtractionId extractionId, final File file) {
        registerFileOfInterest(file, extractionId.toUniqueString());
    }

    public void registerGlobalFileOfInterest(final File file) {
        registerFileOfInterest(file, "global");
    }

    private void registerFileOfInterest(final File file, final String directoryName) {
        try {
            if (file == null) {
                return;
            }
            if (isChildOfTrackedFolder(file)) {
                logger.info("Asked to track file '" + file.getPath() + "' but it is already being tracked.");
                return;
            }
            if (file.isFile()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyFile(file, dest);
            } else if (file.isDirectory()) {
                final File dest = findNextAvailableRelevant(directoryName, file.getName());
                FileUtils.copyDirectory(file, dest);
            }
        } catch (final Exception e) {
            logger.trace("Failed to copy file to relevant directory:" + file.toString());
        }
    }

    private boolean isChildOfTrackedFolder(final File file) {
        final Path filePath = file.toPath();
        return trackedDirectories.stream().anyMatch(trackedFile -> filePath.startsWith(trackedFile.toPath()));
    }

    private File findNextAvailableRelevant(final String directoryName, final String name) {
        final File given = new File(new File(relevantDirectory, directoryName), name);
        if (given.exists()) {
            return findNextAvailableRelevant(directoryName, name, 1);
        } else {
            return given;
        }
    }

    private File findNextAvailableRelevant(final String directoryName, final String name, final int attempt) {
        final File next = new File(new File(relevantDirectory, directoryName), name + "_" + attempt);
        if (next.exists()) {
            return findNextAvailableRelevant(directoryName, name, attempt + 1);
        } else {
            return next;
        }
    }
}
