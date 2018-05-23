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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DetectFileFinder {
    private final Logger logger = LoggerFactory.getLogger(DetectFileFinder.class);

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

    public List<File> findFilesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findFilesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public List<File> findDirectoriesContainingDirectoriesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findDirectoriesContainingDirectoriesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public boolean containsAllFiles(final String sourcePath, final String... filenamePatterns) {
        if (StringUtils.isBlank(sourcePath)) {
            return false;
        }
        final File sourceDirectory = new File(sourcePath);
        return containsAllFiles(sourceDirectory, filenamePatterns);
    }

    public boolean containsAllFiles(final File sourceDirectory, final String... filenamePatterns) {
        if (!sourceDirectory.isDirectory()) {
            return false;
        }

        boolean containsFiles = true;
        for (final String filenamePattern : filenamePatterns) {
            final File foundFile = findFile(sourceDirectory, filenamePattern);
            if (foundFile == null) {
                containsFiles = false;
                logger.debug(String.format("No file detected: %s in %s", filenamePattern, sourceDirectory.getAbsolutePath()));
                break;
            }
        }
        return containsFiles;
    }

    public boolean containsAllFilesToDepth(final String sourcePath, final int maxDepth, final String... filenamePatterns) {
        final File sourceDirectory = new File(sourcePath);
        if (StringUtils.isBlank(sourcePath) || !sourceDirectory.isDirectory()) {
            return false;
        }

        boolean containsFiles = true;
        for (final String filenamePattern : filenamePatterns) {
            final List<File> foundFiles = findFilesToDepth(sourceDirectory, filenamePattern, maxDepth);
            if (null == foundFiles || foundFiles.isEmpty()) {
                containsFiles = false;
                logger.debug(String.format("No file detected: %s in %s", filenamePattern, sourcePath));
                break;
            }
        }
        return containsFiles;
    }

    public File findFile(final String sourcePath, final String filenamePattern) {
        final File sourceDirectory = new File(sourcePath);
        return findFile(sourceDirectory, filenamePattern);
    }

    public File findFile(final File sourceDirectory, final String filenamePattern) {
        final List<File> foundFiles = findFiles(sourceDirectory, filenamePattern);
        if (foundFiles == null || foundFiles.isEmpty()) {
            logger.debug(String.format("Could not find any matches for %s in %s", filenamePattern, sourceDirectory.getAbsolutePath()));
            return null;
        } else if (foundFiles.size() > 1) {
            logger.debug(String.format("Found multiple matches for %s in %s", filenamePattern, sourceDirectory.getAbsolutePath()));
            logger.debug(String.format("Using %s", foundFiles.get(0)));
        }
        return foundFiles.get(0);
    }

    public List<File> findFiles(final File sourceDirectory, final String filenamePattern) {
        if (!sourceDirectory.isDirectory()) {
            return null;
        }
        final File[] foundFiles = sourceDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File directoryContainingTheFile, final String filename) {
                return FilenameUtils.wildcardMatchOnSystem(filename, filenamePattern);
            }
        });
        if (foundFiles == null || foundFiles.length == 0) {
            return null;
        }
        return Arrays.asList(foundFiles);
    }

    public List<File> findFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findFilesRecursive(sourceDirectory, 0, maxDepth, true, filenamePattern);
    }

    public List<File> findAllFilesToMaxDepth(final File sourceDirectory, final String... filenamePatterns) {
        return findFilesRecursive(sourceDirectory, 0, Integer.MAX_VALUE, false, filenamePatterns);
    }

    private List<File> findFilesRecursive(final File sourceDirectory, final int currentDepth, final int maxDepth, Boolean recurseIntoDirectoryMatch, final String... filenamePatterns) {
        final List<File> files = new ArrayList<>();
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory() || null == filenamePatterns || filenamePatterns.length < 1) {
            return files;
        }
        File[] children = sourceDirectory.listFiles();
        if (null != children && children.length > 0) {
            for (final File file : children) {
                boolean matchFound = false;
                for (String filenamePattern : filenamePatterns) {
                    if (FilenameUtils.wildcardMatchOnSystem(file.getName(), filenamePattern)) {
                        files.add(file);
                        matchFound = true;
                    }
                }
                if (file.isDirectory()) {
                    if (!matchFound || (matchFound && recurseIntoDirectoryMatch)) {
                        // only go into the directory if it is not a match OR it is a match and the flag is set to go into matching directories
                        files.addAll(findFilesRecursive(file, currentDepth + 1, maxDepth, recurseIntoDirectoryMatch, filenamePatterns));
                    }
                }
            }
        }
        return files;
    }

    public List<File> findDirectoriesContainingDirectoriesToDepth(final File sourceDirectory, final String directoryPattern, final int maxDepth) {
        return findDirectoriesContainingDirectoriesToDepthRecursive(sourceDirectory, directoryPattern, 0, maxDepth);
    }

    private List<File> findDirectoriesContainingDirectoriesToDepthRecursive(final File sourceDirectory, final String directoryPattern, final int currentDepth, final int maxDepth) {
        final List<File> files = new ArrayList<>();
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory()) {
            return files;
        }
        for (final File file : sourceDirectory.listFiles()) {
            if (file.isDirectory()) {
                if (FilenameUtils.wildcardMatchOnSystem(file.getName(), directoryPattern)) {
                    files.add(file);
                } else {
                    files.addAll(findDirectoriesContainingDirectoriesToDepthRecursive(file, directoryPattern, currentDepth + 1, maxDepth));
                }
            }
        }
        return files;
    }

    public List<File> findDirectoriesContainingFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findDirectoriesContainingFilesRecursive(sourceDirectory, filenamePattern, 0, maxDepth);
    }

    private List<File> findDirectoriesContainingFilesRecursive(final File sourceDirectory, final String filenamePattern, final int currentDepth, final int maxDepth) {
        final Set<File> files = new HashSet<>();
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory()) {
            return new ArrayList<>(files);
        }
        for (final File file : sourceDirectory.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(findDirectoriesContainingFilesRecursive(file, filenamePattern, currentDepth + 1, maxDepth));
            } else if (FilenameUtils.wildcardMatchOnSystem(file.getName(), filenamePattern)) {
                files.add(sourceDirectory);
            }
        }
        return new ArrayList<>(files);
    }

}
