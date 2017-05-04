/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.util

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FileFinder {
    private final Logger logger = LoggerFactory.getLogger(FileFinder.class)

    boolean containsAllFiles(final String sourcePath, final String... filenamePatterns) {
        containsAllFiles(sourcePath, Arrays.asList(filenamePatterns))
    }

    boolean containsAllFiles(final String sourcePath, final List<String> filenamePatterns) {
        if (StringUtils.isBlank(sourcePath)) {
            return false
        }

        final File sourceDirectory = new File(sourcePath)
        boolean containsFiles = true
        for (final String filenamePattern : filenamePatterns) {
            final File foundFile = findFile(sourceDirectory, filenamePattern)
            if (foundFile == null) {
                containsFiles = false
                logger.warn("Couldn't find a neccesary file: ${filenamePattern} in ${sourcePath}")
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
        if (!sourceDirectory.isDirectory()) {
            return null
        }

        File[] foundFiles = sourceDirectory.listFiles(new FilenameFilter() {
                    boolean accept(File directoryContainingTheFile, String filename) {
                        return FilenameUtils.wildcardMatchOnSystem(filename, filenamePattern)
                    }
                })
        if (foundFiles.length == 0) {
            return null
        } else if (foundFiles.length > 1) {
            logger.warn("Found multiple matches for ${filenamePattern} in ${sourceDirectory.absolutePath}")
        }

        foundFiles[0]
    }

    String findExecutablePath(final String executable) {
        String command = "which"
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            command = "where"
        }

        def pathToExecutable = "${command} ${executable}".execute().text.trim()
        if (pathToExecutable) {
            return pathToExecutable
        }

        String systemPath = System.getenv("PATH");
        return findExecutablePath(executable, systemPath)
    }

    String findExecutablePath(final String executable, final String path) {
        for (String pathPiece : path.split(File.pathSeparator)) {
            File foundFile = findFile(pathPiece, executable)
            if (foundFile && foundFile.canExecute()) {
                return foundFile.absolutePath
            }
        }

        null
    }
}
