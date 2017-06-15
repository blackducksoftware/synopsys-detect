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
class FileHelper {
    final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    DetectProperties detectProperties

    File getOutputDirectory(BomToolType bomToolType) {
        def outputDirectory = new File(detectProperties.outputDirectoryPath, bomToolType.toString().toLowerCase())
        outputDirectory.mkdir()

        outputDirectory
    }

    File createTempFile(BomToolType bomToolType, String fileName) {
        File outputDirectory = getOutputDirectory(bomToolType)

        new File(outputDirectory, fileName)
    }

    File writeToTempFile(File file, String contents) {
        writeToTempFile(file, contents, true)
    }

    File writeToTempFile(File file, String contents, boolean overwrite) {
        if(!file) {
            return null
        }
        if(detectProperties.cleanupBomtoolFiles) {
            file.deleteOnExit()
        }
        if(overwrite) {
            file.delete()
        }
        if (file.exists()) {
            logger.info("${file.getAbsolutePath()} exists and not being overwritten")
        } else {
            file << contents
        }

        file
    }
}
