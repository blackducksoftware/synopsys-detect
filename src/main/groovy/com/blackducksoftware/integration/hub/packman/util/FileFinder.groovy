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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
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
                logger.info("No file detected: ${filenamePattern} in ${sourcePath}")
                break
            }
        }
        return containsFiles
    }

    boolean canFindAllExecutables(final Map<String, List<String>> executables) {
        return canFindAllExecutables(executables, null)
    }

    boolean canFindAllExecutables(final Map<String, List<String>> executables, String path) {
        Map<String, String> foundExecutables = findExecutables(executables, path)
        def foundAll = true
        executables.each { executable ->
            if(!foundExecutables[executable.key]) {
                logger.info("No executable detected: ${executable.value}")
                foundAll = false
            }
        }
        return foundAll
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
            logger.info("Found multiple matches for ${filenamePattern} in ${sourceDirectory.absolutePath}")
            logger.info("Using ${foundFiles[0]}")
        }

        foundFiles[0]
    }
}
