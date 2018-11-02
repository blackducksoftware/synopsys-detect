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
package com.blackducksoftware.integration.hub.detect.workflow.file;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;

public class DirectoryManager {
    private final Logger logger = LoggerFactory.getLogger(DirectoryManager.class);

    private enum OutputDirectory {
        Shared("shared"),
        Runs("runs"),
        Tools("tools");

        private String directoryName;

        OutputDirectory(String directoryName) {
            this.directoryName = directoryName;
        }

        public String getDirectoryName() {
            return directoryName;
        }
    }

    private enum RunDirectory {
        Report("reports"),
        Extraction("extractions"),
        Log("logs"),
        Relevant("relevant"),
        Scan("scan"),
        Docker("docker"),
        Bdio("bdio");

        private String directoryName;

        RunDirectory(String directoryName) {
            this.directoryName = directoryName;
        }

        public String getDirectoryName() {
            return directoryName;
        }
    }

    private final File runDirectory;
    private final File sourceDirectory;

    private final Map<OutputDirectory, File> outputDirectories = new HashMap<>();
    private final Map<RunDirectory, File> runDirectories = new HashMap<>();

    private final Map<ExtractionId, File> extractionDirectories = new HashMap<>();

    private final List<File> temporaryFiles = new ArrayList<>();

    public DirectoryManager(final DirectoryOptions directoryOptions, final DetectRun detectRun) {
        if (StringUtils.isBlank(directoryOptions.getSourcePathOverride())) {
            sourceDirectory = new File(System.getProperty("user.dir"));
        } else {
            sourceDirectory = new File(directoryOptions.getSourcePathOverride());
        }

        File outputDirectory;
        if (StringUtils.isBlank(directoryOptions.getOutputPathOverride())) {
            outputDirectory = new File(System.getProperty("user.home"), "blackduck");
        } else {
            outputDirectory = new File(directoryOptions.getOutputPathOverride());
        }

        EnumSet.allOf(OutputDirectory.class).stream()
            .forEach(it -> outputDirectories.put(it, new File(outputDirectory, it.getDirectoryName())));

        runDirectory = new File(getOutputDirectory(OutputDirectory.Runs), detectRun.getRunId());

        EnumSet.allOf(RunDirectory.class).stream()
            .forEach(it -> runDirectories.put(it, new File(runDirectory, it.getDirectoryName())));

        //overrides
        if (StringUtils.isNotBlank(directoryOptions.getBdioOutputPathOverride())) {
            runDirectories.put(RunDirectory.Bdio, new File(directoryOptions.getBdioOutputPathOverride()));
        }

        if (StringUtils.isNotBlank(directoryOptions.getScanOutputPathOverride())) {
            runDirectories.put(RunDirectory.Scan, new File(directoryOptions.getScanOutputPathOverride()));
        }

        runDirectories.values().forEach(it -> temporaryFiles.add(it));

    }

    public File getExtractionOutputDirectory(final ExtractionId extractionId) {
        if (extractionDirectories.containsKey(extractionId)) {
            return extractionDirectories.get(extractionId);
        } else {
            final String directoryName = extractionId.toUniqueString();
            final File newDirectory = new File(getRunDirectory(RunDirectory.Extraction), directoryName);
            newDirectory.mkdir();
            extractionDirectories.put(extractionId, newDirectory);
            return newDirectory;
        }
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getBdioOutputDirectory() {
        return getRunDirectory(RunDirectory.Bdio);
    }

    public File getRunsOutputDirectory() {
        return getOutputDirectory(OutputDirectory.Runs);
    }

    public File getScanOutputDirectory() {
        return getRunDirectory(RunDirectory.Scan);
    }

    public File getDockerOutputDirectory() {
        return getRunDirectory(RunDirectory.Docker);
    }

    public File getRelevantOutputDirectory() {
        return getRunDirectory(RunDirectory.Relevant);
    }

    public File getReportOutputDirectory() {
        return getRunDirectory(RunDirectory.Report);
    }

    public File getLogOutputDirectory() {
        return getRunDirectory(RunDirectory.Log);
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

    public File getSharedDirectory(final String name) { // shared across this invocation of detect (inspectors), returns 'shared/name'
        final File newSharedFile = new File(getOutputDirectory(OutputDirectory.Shared), name);
        newSharedFile.mkdirs();
        return newSharedFile;
    }

    public File getSharedFile(final String sharedDirectory, String fileName) { // helper method for shared files, returns 'shared/name/file'
        return new File(getSharedDirectory(sharedDirectory), fileName);
    }

    public File getPermanentDirectory() { // shared across all invocations of detect (scan cli)
        return getOutputDirectory(OutputDirectory.Tools);
    }

}
