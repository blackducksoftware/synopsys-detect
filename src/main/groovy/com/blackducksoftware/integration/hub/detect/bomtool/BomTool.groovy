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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.springframework.beans.factory.annotation.Autowired

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

import groovy.transform.TypeChecked

@TypeChecked
abstract class BomTool {
    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableManager executableManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    abstract BomToolType getBomToolType()
    abstract boolean isBomToolApplicable()

    /**
     * A BomTool is responsible for doing its best to create at least one, but possibly many, DetectCodeLocations.
     */
    List<DetectCodeLocation> extractDetectCodeLocations() {
        []
    }

    /**
     * BomTool's should override this method if they can determine the correct project name and version
     */
    List<DetectCodeLocation> extractDetectCodeLocations(DetectProject detectProject) {
        extractDetectCodeLocations()
    }

    String getSourcePath() {
        detectConfiguration.sourcePath
    }

    File getSourceDirectory() {
        detectConfiguration.sourceDirectory
    }

    String findExecutablePath(ExecutableType executable, boolean searchSystemPath, String userPath) {
        if (!userPath?.trim()) {
            return executableManager.getExecutablePath(executable, searchSystemPath, sourcePath)
        }

        userPath
    }
}
