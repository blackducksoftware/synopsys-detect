/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.workflow.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectFileFinder {
    private final Logger logger = LoggerFactory.getLogger(DetectFileFinder.class);

    public String extractFinalPieceFromPath(final String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        final String normalizedPath = FilenameUtils.normalizeNoEndSeparator(path, true);
        return normalizedPath.substring(normalizedPath.lastIndexOf("/") + 1, normalizedPath.length());
    }

    public List<File> findFilesToDepth(final String sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findFilesToDepth(new File(sourceDirectory), filenamePattern, maxDepth);
    }

    public boolean containsAllFiles(final String sourceDirectory, final String... filenamePatterns) {
        return containsAllFiles(new File(sourceDirectory), filenamePatterns);
    }

    public boolean containsAllFiles(final File sourceDirectory, final String... filenamePatterns) {
        if (!sourceDirectory.isDirectory()) {
            return false;
        }

        return Arrays.stream(filenamePatterns).allMatch(pattern -> findFile(sourceDirectory, pattern) != null);
    }

    public boolean containsAllFilesToDepth(final String sourcePath, final int maxDepth, final String... filenamePatterns) {
        final File sourceDirectory = new File(sourcePath);
        if (StringUtils.isBlank(sourcePath) || !sourceDirectory.isDirectory()) {
            return false;
        }

        return Arrays.stream(filenamePatterns).map(pattern -> findFilesToDepth(sourceDirectory, pattern, maxDepth)).allMatch(foundFiles -> !foundFiles.isEmpty());
    }

    public File findFile(final String sourceDirectory, final String filenamePattern) {
        return findFile(new File(sourceDirectory), filenamePattern);
    }

    public File findFile(final File sourceDirectory, final String filenamePattern) {
        final File foundFile;
        final List<File> foundFiles = findFiles(sourceDirectory, filenamePattern);
        if (foundFiles == null || foundFiles.isEmpty()) {
            logger.debug(String.format("Could not find any matches for %s in %s", filenamePattern, sourceDirectory.getAbsolutePath()));
            foundFile = null;
        } else {
            foundFile = foundFiles.get(0);
            if (foundFiles.size() > 1) {
                logger.debug(String.format("Found multiple matches for %s in %s", filenamePattern, sourceDirectory.getAbsolutePath()));
                logger.debug(String.format("Using %s", foundFile));
            } else {
                logger.debug(String.format("Found a match %s for file %s in %s", foundFile.getAbsolutePath(), filenamePattern, sourceDirectory.getAbsolutePath()));
            }
        }
        return foundFile;
    }

    public List<File> findFiles(final File sourceDirectory, final String filenamePattern) {
        if (!sourceDirectory.isDirectory()) {
            return null;
        }
        final File[] foundFiles = sourceDirectory.listFiles((FilenameFilter) (directoryContainingTheFile, filename) -> FilenameUtils.wildcardMatchOnSystem(filename, filenamePattern));
        if (foundFiles == null || foundFiles.length == 0) {
            return null;
        }
        return Arrays.asList(foundFiles);
    }

    public List<File> findFilesToDepth(final File sourceDirectory, final String filenamePattern, final int maxDepth) {
        return findFilesRecursive(sourceDirectory, 0, maxDepth, null, true, filenamePattern);
    }

    /**
     * Will recursively look for files/directories matching these name patterns within the source directory. It will not look for matching files/directories within a directory that matched one of the patterns.
     */
    public List<File> findAllFilesToMaxDepth(final File sourceDirectory, final String... filenamePatterns) {
        return findFilesRecursive(sourceDirectory, 0, Integer.MAX_VALUE, null, false, filenamePatterns);
    }

    public List<File> findAllFilesToDepth(final File sourceDirectory, final StringBuilder maxDepthHitMsgPattern, final int maxDepth, final String... filenamePatterns) {
        return findFilesRecursive(sourceDirectory, 0, maxDepth, maxDepthHitMsgPattern, false, filenamePatterns);
    }

    public List<File> findDirectoriesContainingDirectoriesToDepth(final String sourcePath, final String filenamePattern, final int maxDepth) {
        return findDirectoriesContainingDirectoriesToDepth(new File(sourcePath), filenamePattern, maxDepth);
    }

    public List<File> findDirectoriesContainingDirectoriesToDepth(final File sourceDirectory, final String directoryPattern, final int maxDepth) {
        return findDirectoriesContainingDirectoriesToDepthRecursive(sourceDirectory, directoryPattern, 0, maxDepth);
    }

    private List<File> findFilesRecursive(final File sourceDirectory, final int currentDepth, final int maxDepth, StringBuilder maxDepthHitMsgPattern, final Boolean recurseIntoDirectoryMatch, final String... filenamePatterns) {
        final List<File> files = new ArrayList<>();
        if (currentDepth >= maxDepth) {
            if (StringUtils.isNotBlank(maxDepthHitMsgPattern)) {
                logger.warn(String.format(maxDepthHitMsgPattern.toString(), sourceDirectory.getAbsolutePath()));
                // Ensure msg only shown once
                maxDepthHitMsgPattern.setLength(0);
            }
        } else if (sourceDirectory.isDirectory()) {
            File[] children = sourceDirectory.listFiles();
            if (children != null && children.length > 0 && null != filenamePatterns && filenamePatterns.length >= 1) {
                for (final File file : children) {
                    final boolean fileMatchesPatterns = Arrays.stream(filenamePatterns).anyMatch(pattern -> FilenameUtils.wildcardMatchOnSystem(file.getName(), pattern));

                    if (fileMatchesPatterns) {
                        files.add(file);
                    }

                    if (file.isDirectory() && (!fileMatchesPatterns || recurseIntoDirectoryMatch)) {
                        // only go into the directory if it is not a match OR it is a match and the flag is set to go into matching directories
                        files.addAll(findFilesRecursive(file, currentDepth + 1, maxDepth, maxDepthHitMsgPattern, recurseIntoDirectoryMatch, filenamePatterns));
                    }
                }
            } else if (children == null) {
                logger.warn("Directory contents could not be accessed: " + sourceDirectory.getAbsolutePath());
            }
        }
        return files;
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

    public File findContainingDir(final File givenDir, int numberOfLevelsToWalkBack) {
        File containingDir = givenDir;
        for (; numberOfLevelsToWalkBack > 0; numberOfLevelsToWalkBack--) {
            containingDir = containingDir.getParentFile();
        }
        return containingDir;
    }

    public boolean isFileUnderDir(final File dir, final File file) {
        try {
            final String dirPath = dir.getCanonicalPath();
            final String filePath = file.getCanonicalPath();
            if (filePath.startsWith(dirPath)) {
                return true;
            }
            return false;
        } catch (final IOException e) {
            logger.warn(String.format("Error getting canonical path for either %s or %s", dir.getAbsolutePath(), file.getAbsolutePath()));
            return false;
        }
    }
}
