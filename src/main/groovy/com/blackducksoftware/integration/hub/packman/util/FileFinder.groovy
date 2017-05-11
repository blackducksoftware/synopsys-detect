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

import com.blackducksoftware.integration.hub.packman.util.commands.Executable

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
                logger.info("Couldn't find a neccesary file: ${filenamePattern} in ${sourcePath}")
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
            logger.info("Found multiple matches for ${filenamePattern} in ${sourceDirectory.absolutePath}")
            logger.info("Using ${foundFiles[0]}")
        }

        foundFiles[0]
    }

    Map<String, Executable> findExecutables(final Map<String, List<String>> executables) {
        findExecutables(executables, null)
    }

    Map<String, Executable> findExecutables(final Map<String, List<String>> executables, String path) {
        Map<String, Executable> found = [:]
        executables.each { executableName, executableList ->
            for(String executable : executableList) {
                Executable current = findExecutable(executable, path)
                if(current != null) {
                    found[executableName] = current
                }
            }
        }
        found
    }

    Executable findExecutable(final String executable) {
        def found = findExecutablePath(executable, null)
    }

    Executable findExecutable(final String executable, final String path) {
        def found = null
        if(path) {
            found = findExecutablePath(executable, path)
        } else {
            found = findExecutablePath(executable)
        }

        if(found) {
            return new Executable(executable, found)
        }
        null
    }

    String findExecutablePath(final String executable) {
        String systemPath = System.getenv("PATH")
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

    Map<String, String> findFolders(Map<String, List<String>> folderMap, String path) {
        def newFolderMap = [:]
        folderMap.each {
            for(String folderName : it.value) {
                def parentFolder = new File(path)
                def folder = new File(parentFolder, folderName)
                if (folder.exists() && folder.isDirectory()) {
                    newFolderMap[it.key] = folder.getAbsolutePath()
                    break
                }
            }
        }
        newFolderMap
    }
}
