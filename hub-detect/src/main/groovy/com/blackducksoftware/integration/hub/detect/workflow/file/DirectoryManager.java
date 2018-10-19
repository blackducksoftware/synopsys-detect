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

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
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
        Scan("scan", DetectProperty.DETECT_SCAN_OUTPUT_PATH),
        Bdio("bdio", DetectProperty.DETECT_BDIO_OUTPUT_PATH);

        private String directoryName;
        private DetectProperty overrideProperty;

        RunDirectory(String directoryName) {
            this.directoryName = directoryName;
        }

        RunDirectory(String directoryName, DetectProperty overrideProperty) {
            this.directoryName = directoryName;
            this.overrideProperty = overrideProperty;
        }

        public String getDirectoryName() {
            return directoryName;
        }

        public DetectProperty getOverrideProperty() {
            return overrideProperty;
        }

    }

    private final File runDirectory;
    private final File sourceDirectory;

    private final Map<OutputDirectory, File> outputDirectories = new HashMap<>();
    private final Map<RunDirectory, File> runDirectories = new HashMap<>();

    private final Map<ExtractionId, File> extractionDirectories = new HashMap<>();

    private final List<File> temporaryFiles = new ArrayList<>();

    public DirectoryManager(final DetectConfiguration detectConfiguration, final DetectRun detectRun) {

        String rawSource = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.None);
        if (StringUtils.isBlank(rawSource)) {
            sourceDirectory = new File(System.getProperty("user.dir"));
        } else {
            sourceDirectory = new File(rawSource);
        }

        File outputDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_OUTPUT_PATH, PropertyAuthority.None));

        EnumSet.allOf(OutputDirectory.class).stream()
            .forEach(it -> outputDirectories.put(it, new File(outputDirectory, it.getDirectoryName())));

        runDirectory = new File(getOutputDirectory(OutputDirectory.Runs), detectRun.getRunId());

        EnumSet.allOf(RunDirectory.class).stream()
            .forEach(it -> initRunDirectory(it, detectConfiguration));

        runDirectories.values().forEach(it -> temporaryFiles.add(it));

    }

    private void initRunDirectory(RunDirectory givenDirectory, DetectConfiguration detectConfiguration) {
        File givenFile = null;
        if (givenDirectory.getOverrideProperty() != null) {
            String override = detectConfiguration.getProperty(givenDirectory.getOverrideProperty(), PropertyAuthority.None);
            if (StringUtils.isNotBlank(override)) {
                givenFile = new File(override);
            }
        }
        if (givenFile == null) {
            givenFile = new File(runDirectory, givenDirectory.getDirectoryName());
        }
        runDirectories.put(givenDirectory, givenFile);
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

    public File getRelevantDirectory() {
        return getRunDirectory(RunDirectory.Relevant);
    }

    public File getReportDirectory() {
        return getRunDirectory(RunDirectory.Report);
    }

    public File getLogDirectory() {
        return getRunDirectory(RunDirectory.Log);
    }

    private File getOutputDirectory(OutputDirectory directory) {
        File actualDirectory = outputDirectories.get(directory);
        if (!actualDirectory.exists()) {
            actualDirectory.mkdir();
        }
        return actualDirectory;
    }

    private File getRunDirectory(RunDirectory directory) {
        File actualDirectory = runDirectories.get(directory);
        if (!actualDirectory.exists()) {
            actualDirectory.mkdir();
        }
        return actualDirectory;
    }

    public File getSharedDirectory(final String name) { // shared across this invocation of detect (inspectors), returns 'shared/name'
        final File newSharedFile = new File(getOutputDirectory(OutputDirectory.Shared), name);
        newSharedFile.mkdir();
        return newSharedFile;
    }

    public File getSharedFile(final String sharedDirectory, String fileName) { // helper method for shared files, returns 'shared/name/file'
        return new File(getSharedDirectory(sharedDirectory), fileName);
    }

    public File getPermanentDirectory() { // shared across all invocations of detect (scan cli)
        return getOutputDirectory(OutputDirectory.Tools);
    }
}
