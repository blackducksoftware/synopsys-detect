/*
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
package com.blackducksoftware.integration.hub.detect.util

import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import groovy.transform.TypeChecked

@Component
@TypeChecked
class FileFinder {
    private final Logger logger = LoggerFactory.getLogger(FileFinder.class)

    boolean containsAllFiles(final String sourcePath, final String... filenamePatterns) {
        final File sourceDirectory = new File(sourcePath)
        if (!sourcePath || !sourceDirectory.isDirectory()) {
            return false
        }

        boolean containsFiles = true
        for (final String filenamePattern : filenamePatterns) {
            final File foundFile = findFile(sourceDirectory, filenamePattern)
            if (foundFile == null) {
                containsFiles = false
                logger.debug("No file detected: ${filenamePattern} in ${sourcePath}")
                break
            }
        }
        return containsFiles
    }

    boolean containsAllFilesToDepth(final String sourcePath, int maxDepth, final String... filenamePatterns) {
        final File sourceDirectory = new File(sourcePath)
        if (!sourcePath || !sourceDirectory.isDirectory()) {
            return false
        }

        boolean containsFiles = true
        for (final String filenamePattern : filenamePatterns) {
            def foundFiles = findFilesToDepth(sourceDirectory, filenamePattern, maxDepth)
            if (!foundFiles) {
                containsFiles = false
                logger.debug("No file detected: ${filenamePattern} in ${sourcePath}")
                break
            }
        }
        return containsFiles
    }

    File findFile(final String sourcePath, final String filenamePattern) {
        File sourceDirectory = new File(sourcePath)
        findFile(sourceDirectory, filenamePattern)
    }

    File findFile(final File sourceDirectory, final String filenamePattern) {
        File[] foundFiles = findFiles(sourceDirectory, filenamePattern)
        if (foundFiles == null || foundFiles.length == 0) {
            logger.debug("Could not find any matches for ${filenamePattern} in ${sourceDirectory.absolutePath}")
            return null
        } else if (foundFiles.length > 1) {
            logger.debug("Found multiple matches for ${filenamePattern} in ${sourceDirectory.absolutePath}")
            logger.debug("Using ${foundFiles[0]}")
        }
        foundFiles[0]
    }

    File[] findFiles(final File sourceDirectory, final String filenamePattern) {
        if (!sourceDirectory.isDirectory()) {
            return null
        }
        File[] foundFiles = sourceDirectory.listFiles(new FilenameFilter() {
                    boolean accept(File directoryContainingTheFile, String filename) {
                        return FilenameUtils.wildcardMatchOnSystem(filename, filenamePattern)
                    }
                })
        if (foundFiles == null || foundFiles.length == 0) {
            return null
        }
        foundFiles
    }

    File[] findFilesToDepth(final File sourceDirectory, final String filenamePattern, int maxDepth) {
        return findFilesRecursive(sourceDirectory, filenamePattern, 0, maxDepth)
    }

    private File[] findFilesRecursive(final File sourceDirectory, final String filenamePattern, int currentDepth, int maxDepth) {
        def files = []
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory()) {
            return files as File[]
        }
        sourceDirectory.listFiles().each { File file ->
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), filenamePattern)) {
                files.add(file)
            }
            if (file.isDirectory()) {
                files.addAll(findFilesRecursive(file, filenamePattern, currentDepth + 1, maxDepth))
            }
        }
        return files as File[]
    }

    public File[] findDirectoriesContainingDirectoriesToDepth(final File sourceDirectory, final String directoryPattern, int maxDepth) {
        findDirectoriesContainingDirectoriesToDepthRecursive(sourceDirectory, directoryPattern, 0, maxDepth)
    }

    private File[] findDirectoriesContainingDirectoriesToDepthRecursive(final File sourceDirectory, final String directoryPattern, int currentDepth, int maxDepth) {
        def files = []
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory()) {
            return files as File[]
        }

        sourceDirectory.listFiles().each { File file ->
            if (file.isDirectory()) {
                if (FilenameUtils.wildcardMatchOnSystem(file.getName(), directoryPattern)) {
                    files.add(file)
                } else {
                    files.addAll(findDirectoriesContainingDirectoriesToDepthRecursive(file, directoryPattern, currentDepth + 1, maxDepth))
                }
            }
        }

        return files as File[]
    }

    File[] findDirectoriesContainingFilesToDepth(final File sourceDirectory, final String filenamePattern, int maxDepth) {
        return findDirectoriesContainingFilesRecursive(sourceDirectory, filenamePattern, 0, maxDepth)
    }

    private File[] findDirectoriesContainingFilesRecursive(final File sourceDirectory, final String filenamePattern, int currentDepth, int maxDepth) {
        def files = new HashSet<File>()
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory()) {
            return files as File[]
        }
        for (File file : sourceDirectory.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(findDirectoriesContainingFilesRecursive(file, filenamePattern, currentDepth + 1, maxDepth))
            } else if (FilenameUtils.wildcardMatchOnSystem(file.getName(), filenamePattern)) {
                files.add(sourceDirectory)
            }
        }
        return new ArrayList<File>(files) as File[]
    }
}
