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

    boolean containsAllFilesWithDepth(final String sourcePath, int maxDepth, final String... filenamePatterns) {
        final File sourceDirectory = new File(sourcePath)
        if (!sourcePath || !sourceDirectory.isDirectory()) {
            return false
        }

        boolean containsFiles = true
        for (final String filenamePattern : filenamePatterns) {
            def foundFiles = findFilesToDepth(sourceDirectory, filenamePattern, maxDepth)
            if (!foundFiles) {
                containsFiles = false
                logger.info("No file detected: ${filenamePattern} in ${sourcePath}")
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
            return null
        } else if (foundFiles.length > 1) {
            logger.info("Found multiple matches for ${filenamePattern} in ${sourceDirectory.absolutePath}")
            logger.info("Using ${foundFiles[0]}")
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
        if (foundFiles.length == 0) {
            return null
        }
        foundFiles
    }

    File[] findFilesToDepth(final File sourceDirectory, final String filenamePattern, int maxDepth){
        return findFilesRecursive(sourceDirectory, filenamePattern, 0, maxDepth)
    }

    private File[] findFilesRecursive(final File sourceDirectory, final String filenamePattern, int currentDepth, int maxDepth){
        def files = [];
        if(currentDepth >= maxDepth){
            return files
        }
        if (!sourceDirectory.isDirectory()) {
            return null
        }
        sourceDirectory.listFiles().each {
            if(it.isDirectory()){
                files.addAll(findFilesRecursive(it, filenamePattern, currentDepth++, maxDepth))
            }else if(FilenameUtils.wildcardMatchOnSystem(it.getName(), filenamePattern)){
                files.add(it)
            }
        }
        return files
    }

    File[] findDirectoriesContainingFilesToDepth(final File sourceDirectory, final String filenamePattern, int maxDepth){
        return findDirectoriesRecursive(sourceDirectory, filenamePattern, 0, maxDepth)
    }

    private File[] findDirectoriesContainingFilesRecursive(final File sourceDirectory, final String filenamePattern, int currentDepth, int maxDepth){
        def files = new HashSet<File>();
        if(currentDepth >= maxDepth){
            return files
        }
        if (!sourceDirectory.isDirectory()) {
            return null
        }
        sourceDirectory.listFiles().each {
            if(it.isDirectory()){
                files.addAll(findFilesRecursive(sourceDirectory, filenamePattern, currentDepth++, maxDepth))
            }else if(FilenameUtils.wildcardMatchOnSystem(it.getName(), filenamePattern)){
                files.add(sourceDirectory)
            }
        }
        return new ArrayList<File>(files)
    }
}
