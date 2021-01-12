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
package com.synopsys.integration.detect.workflow.file;

import java.io.File;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionId;
import com.synopsys.integration.detect.workflow.DetectRun;

public class DirectoryManager {
    private final Logger logger = LoggerFactory.getLogger(DirectoryManager.class);

    private enum OutputDirectory {
        RUNS("runs"),
        TOOLS("tools");

        private final String directoryName;

        OutputDirectory(String directoryName) {
            this.directoryName = directoryName;
        }

        public String getDirectoryName() {
            return directoryName;
        }
    }

    private enum RunDirectory {
        BDIO("bdio"),
        BINARY("binary"),
        EXTRACTION("extractions"),
        IMPACT_ANALYSIS("impact-analysis"),
        LOG("logs"),
        EXECUTABLES("executables"),
        RELEVANT("relevant"),
        REPORT("reports"),
        SCAN("scan"),
        SHARED("shared"),
        STATUS("status");

        private final String directoryName;

        RunDirectory(String directoryName) {
            this.directoryName = directoryName;
        }

        public String getDirectoryName() {
            return directoryName;
        }
    }

    private final File userHome;
    private final File runDirectory;
    private final File sourceDirectory;

    private final Map<OutputDirectory, File> outputDirectories = new HashMap<>();
    private final Map<RunDirectory, File> runDirectories = new HashMap<>();

    private final Map<ExtractionId, File> extractionDirectories = new HashMap<>();

    public DirectoryManager(DirectoryOptions directoryOptions, DetectRun detectRun) {
        sourceDirectory = directoryOptions.getSourcePathOverride()
                              .map(Path::toFile)
                              .orElse(new File(System.getProperty("user.dir")));
        logger.info("Source directory: " + sourceDirectory.getAbsolutePath());

        userHome = new File(System.getProperty("user.home"));

        File outputDirectory = directoryOptions.getOutputPathOverride()
                                   .map(Path::toFile)
                                   .orElse(new File(userHome, "blackduck"));
        if (outputDirectory.getAbsolutePath().contains("systemprofile")) {
            logger.warn("You appear to be running in 'systemprofile' which can happen when detect is invoked by a system account or as a service.");
            logger.warn("If detect has full access to the output directory, no further action is necessary.");
            logger.warn("However, this folder typically has restricted access and may cause exceptions in detect.");
            logger.warn("To ensure continued operation, supply an output directory using " + DetectProperties.DETECT_OUTPUT_PATH.getProperty().getName() + " in the future.");
        }
        logger.info("Output directory: " + outputDirectory.getAbsolutePath());

        directoryOptions.getToolsOutputPath()
            .map(Path::toFile)
            .ifPresent(toolPath -> {
                outputDirectories.put(OutputDirectory.TOOLS, toolPath);
                logger.info("Tool directory: " + toolPath.getAbsolutePath());
            });

        EnumSet.allOf(OutputDirectory.class).stream()
            .filter(it -> !outputDirectories.containsKey(it))
            .forEach(it -> outputDirectories.put(it, new File(outputDirectory, it.getDirectoryName())));

        File possibleRunDirectory = new File(getOutputDirectory(OutputDirectory.RUNS), detectRun.getRunId());
        if (possibleRunDirectory.exists()) {
            logger.warn("A run directory already exists with this detect run id. Will attempt to use a UUID for the run folder in addition.");
            possibleRunDirectory = new File(getOutputDirectory(OutputDirectory.RUNS), detectRun.getRunId() + "-" + java.util.UUID.randomUUID());
        }
        runDirectory = possibleRunDirectory;

        logger.info("Run directory: " + runDirectory.getAbsolutePath());

        EnumSet.allOf(RunDirectory.class)
            .forEach(it -> runDirectories.put(it, new File(runDirectory, it.getDirectoryName())));

        //overrides
        directoryOptions.getBdioOutputPathOverride()
            .map(Path::toFile)
            .ifPresent(bdioOutputPath -> runDirectories.put(RunDirectory.BDIO, bdioOutputPath));

        directoryOptions.getScanOutputPathOverride()
            .map(Path::toFile)
            .ifPresent(scanOutputPath -> runDirectories.put(RunDirectory.SCAN, scanOutputPath));
    }

    public File getUserHome() {
        return userHome;
    }

    public File getExtractionOutputDirectory(ExtractionId extractionId) {
        if (extractionDirectories.containsKey(extractionId)) {
            return extractionDirectories.get(extractionId);
        } else {
            String directoryName = extractionId.toUniqueString();
            File newDirectory = new File(getRunDirectory(RunDirectory.EXTRACTION), directoryName);
            newDirectory.mkdir();
            extractionDirectories.put(extractionId, newDirectory);
            return newDirectory;
        }
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getBdioOutputDirectory() {
        return getRunDirectory(RunDirectory.BDIO);
    }

    public File getRunsOutputDirectory() {
        return getOutputDirectory(OutputDirectory.RUNS);
    }

    public File getScanOutputDirectory() {
        return getRunDirectory(RunDirectory.SCAN);
    }

    public File getBinaryOutputDirectory() {
        return getRunDirectory(RunDirectory.BINARY);
    }

    public File getImpactAnalysisOutputDirectory() {
        return getRunDirectory(RunDirectory.IMPACT_ANALYSIS);
    }

    public File getRelevantOutputDirectory() {
        return getRunDirectory(RunDirectory.RELEVANT);
    }

    public File getReportOutputDirectory() {
        return getRunDirectory(RunDirectory.REPORT);
    }

    public File getLogOutputDirectory() {
        return getRunDirectory(RunDirectory.LOG);
    }

    public File getStatusOutputDirectory() {
        return getRunDirectory(RunDirectory.STATUS);
    }

    public File getExecutableOutputDirectory() {
        return getRunDirectory(RunDirectory.EXECUTABLES);
    }

    public File getRunHomeDirectory() {
        return runDirectory;
    }

    private File getOutputDirectory(OutputDirectory directory) {
        File actualDirectory = outputDirectories.get(directory);
        if (!actualDirectory.exists()) {
            actualDirectory.mkdirs();
        }
        return actualDirectory;
    }

    private File getRunDirectory(RunDirectory directory) {
        File actualDirectory = runDirectories.get(directory);
        if (!actualDirectory.exists()) {
            actualDirectory.mkdirs();
        }
        return actualDirectory;
    }

    public File getSharedDirectory(String name) { // shared across this invocation of detect (inspectors), returns 'shared/name'
        File newSharedFile = new File(getRunDirectory(RunDirectory.SHARED), name);
        newSharedFile.mkdirs();
        return newSharedFile;
    }

    public File getSharedFile(String sharedDirectory, String fileName) { // helper method for shared files, returns 'shared/name/file'
        return new File(getSharedDirectory(sharedDirectory), fileName);
    }

    public File getPermanentDirectory() { // shared across all invocations of detect (scan cli)
        return getOutputDirectory(OutputDirectory.TOOLS);
    }

    public File getPermanentDirectory(String name) { // shared across all invocations of detect (scan cli)
        return new File(getOutputDirectory(OutputDirectory.TOOLS), name);
    }

}
