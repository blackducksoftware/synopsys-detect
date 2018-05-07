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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectFileManager {
    private final Logger logger = LoggerFactory.getLogger(DetectFileManager.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    private final String sharedUUID = "shared"; //UUID.randomUUID().toString();
    private File sharedDirectory = null;
    private final Map<ExtractionContext, File> outputDirectories = new HashMap<>();

    private Integer count = 0;
    //New API
    public File getOutputDirectory(final ExtractionContext context) {
        if (outputDirectories.containsKey(context)) {
            return outputDirectories.get(context);
        }else {
            final String directoryName = context.getClass().getSimpleName() + "-" + Integer.toString(context.hashCode()) + "-" + count.toString();

            final File newDirectory = new File(getExtractionFile(), directoryName);
            newDirectory.mkdir();
            count++;
            outputDirectories.put(context, newDirectory);
            return newDirectory;
        }
    }

    private File getExtractionFile() {
        final File newDirectory = new File(detectConfiguration.getOutputDirectory(), "extractions");
        newDirectory.mkdir();
        return newDirectory;
    }

    public File getOutputFile(final ExtractionContext context, final String name) {
        final File directory = getOutputDirectory(context);
        return new File(directory, name);
    }

    public File getSharedDirectory(final String name) { //shared across this invocation of detect.
        if (sharedDirectory == null) {
            sharedDirectory = new File(detectConfiguration.getOutputDirectory(), sharedUUID);
            sharedDirectory.mkdir();
        }
        return sharedDirectory;
    }

    public File getPermanentDirectory() { //shared across all invocations of detect
        final File newDirectory = new File(detectConfiguration.getOutputDirectory(), "tools");
        newDirectory.mkdir();
        return newDirectory;
    }

    public File writeToFile(final File file, final String contents) throws IOException {
        return writeToFile(file, contents, true);
    }


    public File createSharedFile(final String directory, final String filename) {
        return new File(getSharedDirectory(directory), filename);
    }

    public void addOutputFile(final ExtractionContext context, final File file) {
        try {
            if (file.isFile()) {
                final File out = getOutputDirectory(context);
                final File dest = new File(out, file.getName());
                FileUtils.moveFile(file, dest);
            }else if (file.isDirectory()) {
                final File out = getOutputDirectory(context);
                final File dest = new File(out, file.getName());
                FileUtils.moveDirectory(file, dest);
            }
        }catch (final Exception e) {

        }

    }

    public void cleanupDirectories() {
    }

    //Old API
    private final Set<File> directoriesToCleanup = new LinkedHashSet<>();

    private File createDirectory(final BomToolType bomToolType) {
        return createDirectory(bomToolType.toString().toLowerCase(), true);
    }

    private File createDirectory(final String directoryName) {
        return createDirectory(detectConfiguration.getOutputDirectory(), directoryName, true);
    }

    private File createDirectory(final File directory, final String newDirectoryName) {
        return createDirectory(directory, newDirectoryName, true);
    }

    private File createDirectory(final String directoryName, final boolean allowDelete) {
        return createDirectory(detectConfiguration.getOutputDirectory(), directoryName, allowDelete);
    }

    private File createDirectory(final File directory, final String newDirectoryName, final boolean allowDelete) {
        final File newDirectory = new File(directory, newDirectoryName);
        newDirectory.mkdir();
        if (detectConfiguration.getCleanupDetectFiles() && allowDelete) {
            directoriesToCleanup.add(newDirectory);
        }

        return newDirectory;
    }

    private File createFile(final File directory, final String filename) {
        final File newFile = new File(directory, filename);
        if (detectConfiguration.getCleanupDetectFiles()) {
            newFile.deleteOnExit();
        }
        return newFile;
    }

    private File createFile(final BomToolType bomToolType, final String filename) {
        final File directory = createDirectory(bomToolType);
        return createFile(directory, filename);
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
