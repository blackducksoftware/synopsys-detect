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

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.PackagistParser
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

@Component
@groovy.transform.CompileStatic
class PackagistBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(PackagistBomTool.class)

    @Autowired
    PackagistParser packagistParser

    @Override
    public BomToolType getBomToolType() {
        BomToolType.PACKAGIST
    }

    @Override
    public boolean isBomToolApplicable() {
        boolean containsComposerLock = detectFileManager.containsAllFiles(sourcePath, 'composer.lock')
        boolean containsComposerJson = detectFileManager.containsAllFiles(sourcePath, 'composer.json')

        if (containsComposerLock && !containsComposerJson) {
            logger.warn("composer.lock was located in ${sourcePath}, but no composer.json. Please add a composer.json file and try again.")
        } else if (!containsComposerLock && containsComposerJson) {
            logger.warn("composer.json was located in ${sourcePath}, but no composer.lock. Please install dependencies and try again.")
        }

        containsComposerLock && containsComposerJson
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        String composerJsonText = new File(sourcePath, 'composer.json').getText(StandardCharsets.UTF_8.toString())
        String composerLockText = new File(sourcePath, 'composer.lock').getText(StandardCharsets.UTF_8.toString())

        DependencyNode rootDependencyNode = packagistParser.getDependencyNodeFromProject(composerJsonText, composerLockText)
        def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, rootDependencyNode)

        [detectCodeLocation]
    }
}
