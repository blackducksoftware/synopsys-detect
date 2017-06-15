/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class DetectFileService {
    final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    DetectProperties detectProperties

    @Autowired
    FileFinder detectFileService

    File createDirectory(BomToolType bomToolType) {
        def outputDirectory = new File(detectProperties.outputDirectoryPath, bomToolType.toString().toLowerCase())
        outputDirectory.mkdir()

        outputDirectory
    }

    File createFile(BomToolType bomToolType, String fileName) {
        File outputDirectory = createDirectory(bomToolType)

        new File(outputDirectory, fileName)
    }

    File writeToFile(File file, String contents) {
        writeToFile(file, contents, true)
    }

    File writeToFile(File file, String contents, boolean overwrite) {
        if (!file) {
            return null
        }
        if (detectProperties.cleanupBomtoolFiles) {
            file.deleteOnExit()
        }
        if (overwrite) {
            file.delete()
        }
        if (file.exists()) {
            logger.info("${file.getAbsolutePath()} exists and not being overwritten")
        } else {
            file << contents
        }

        file
    }

    public boolean containsAllFiles(String sourcePath, String... filenamePatterns) {
        return detectFileService.containsAllFiles(sourcePath, filenamePatterns)
    }

    public boolean containsAllFilesWithDepth(String sourcePath, int maxDepth, String... filenamePatterns) {
        return detectFileService.containsAllFilesWithDepth(sourcePath, maxDepth, filenamePatterns)
    }

    public File findFile(String sourcePath, String filenamePattern) {
        return detectFileService.findFile(sourcePath, filenamePattern)
    }

    public File findFile(File sourceDirectory, String filenamePattern) {
        return detectFileService.findFile(sourceDirectory, filenamePattern)
    }

    public File[] findFiles(File sourceDirectory, String filenamePattern) {
        return detectFileService.findFiles(sourceDirectory, filenamePattern)
    }

    public File[] findFilesToDepth(File sourceDirectory, String filenamePattern, int maxDepth) {
        return detectFileService.findFilesToDepth(sourceDirectory, filenamePattern, maxDepth)
    }

    public File[] findDirectoriesContainingFilesToDepth(File sourceDirectory, String filenamePattern, int maxDepth) {
        return detectFileService.findDirectoriesContainingFilesToDepth(sourceDirectory, filenamePattern, maxDepth)
    }
}
