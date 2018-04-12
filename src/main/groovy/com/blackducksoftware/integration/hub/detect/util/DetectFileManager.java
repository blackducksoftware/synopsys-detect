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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectFileManager {
    private final Logger logger = LoggerFactory.getLogger(DetectFileManager.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private FileFinder fileFinder;

    private final Set<File> directoriesToCleanup = new LinkedHashSet<>();

    public void cleanupDirectories() {
        if (null != directoriesToCleanup && !directoriesToCleanup.isEmpty()) {
            for (final File directory : directoriesToCleanup) {
                FileUtils.deleteQuietly(directory);
            }
        }
    }

    public File createDirectory(final BomToolType bomToolType) {
        return createDirectory(bomToolType.toString().toLowerCase(), true);
    }

    public File createDirectory(final String directoryName) {
        return createDirectory(detectConfiguration.getOutputDirectory(), directoryName, true);
    }

    public File createDirectory(final File directory, final String newDirectoryName) {
        return createDirectory(directory, newDirectoryName, true);
    }

    public File createDirectory(final String directoryName, final boolean allowDelete) {
        return createDirectory(detectConfiguration.getOutputDirectory(), directoryName, allowDelete);
    }

    public File createDirectory(final File directory, final String newDirectoryName, final boolean allowDelete) {
        final File newDirectory = new File(directory, newDirectoryName);
        newDirectory.mkdir();
        if (detectConfiguration.getCleanupDetectFiles() && allowDelete) {
            directoriesToCleanup.add(newDirectory);
        }

        return newDirectory;
    }

    public File createFile(final File directory, final String filename) {
        final File newFile = new File(directory, filename);
        if (detectConfiguration.getCleanupDetectFiles()) {
            newFile.deleteOnExit();
        }
        return newFile;
    }

    public File createFile(final BomToolType bomToolType, final String filename) {
        final File directory = createDirectory(bomToolType);
        return createFile(directory, filename);
    }

    public File writeToFile(final File file, final String contents) throws IOException {
        return writeToFile(file, contents, true);
    }

    public File writeToFile(final File file, final String contents, final boolean overwrite) throws IOException {
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

    public String extractFinalPieceFromPath(final String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        final String normalizedPath = FilenameUtils.normalizeNoEndSeparator(path, true);
        return normalizedPath.substring(normalizedPath.lastIndexOf("/") + 1, normalizedPath.length());
    }

    public boolean directoryExists(final String sourcePath, final String relativePath) {
        final File sourceDirectory = new File(sourcePath);
        final File relativeDirectory = new File(sourceDirectory, relativePath);
        return relativeDirectory.isDirectory();
    }

    public boolean containsAllFiles(final File sourcePath, final String... filenamePatterns) {
        return fileFinder.containsAllFiles(sourcePath, filenamePatterns);
    }

    public boolean containsAllFiles(final String sourcePath, final String... filenamePatterns) {
        return fileFinder.containsAllFiles(sourcePath, filenamePatterns);
    }

    public boolean containsAllFilesToDepth(final String sourcePath, final int maxDepth, final String... filenamePatterns) {
        return fileFinder.containsAllFilesToDepth(sourcePath, maxDepth, filenamePatterns);
    }

    public File findFile(final String sourcePath, final String filenamePattern) {
        return fileFinder.findFile(sourcePath, filenamePattern);
    }

    public File findFile(final File sourceDirectory, final String filenamePattern) {
        return fileFinder.findFile(sourceDirectory, filenamePattern);
    }

    public List<File> findFiles(final File sourceDirectory, final String filenamePattern) {
        return fileFinder.findFiles(sourceDirectory, filenamePattern);
    }

    public List<File> findFilesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findFilesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public List<File> findFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return fileFinder.findFilesToDepth(sourceDirectory, filenamePattern, maxDepth);
    }

    public List<File> findDirectoriesContainingDirectoriesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return fileFinder.findDirectoriesContainingDirectoriesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public List<File> findDirectoriesContainingFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return fileFinder.findDirectoriesContainingFilesToDepth(sourceDirectory, filenamePattern, maxDepth);
    }

}
