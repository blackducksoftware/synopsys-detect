/*
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
package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PackagistBomTool extends BomTool<PackagistApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(PackagistBomTool.class)

    @Autowired
    PackagistParser packagistParser

    @Autowired
    ExternalIdFactory externalIdFactory

    @Override
    public BomToolType getBomToolType() {
        BomToolType.PACKAGIST
    }

    @Override
    public PackagistApplicableResult isBomToolApplicable(File directory) {
        def composerLock = detectFileManager.findFile(directory, 'composer.lock')
        def composerJson = detectFileManager.findFile(directory, 'composer.json')

        def containsComposerLock = composerLock.exists();
        def containsComposerJson = composerJson.exists();

        if (containsComposerLock && containsComposerJson) {
            return new PackagistApplicableResult(directory, composerLock, composerJson)
        }else if (containsComposerLock && !containsComposerJson) {
            logger.warn("composer.lock was located in ${directory}, but no composer.json. Please add a composer.json file and try again.")
        } else if (!containsComposerLock && containsComposerJson) {
            logger.warn("composer.json was located in ${directory}, but no composer.lock. Please install dependencies and try again.")
        }

        return null;
    }

    BomToolExtractionResult extractDetectCodeLocations(PackagistApplicableResult applicable) {
        String composerJsonText = applicable.composerJson.getText(StandardCharsets.UTF_8.toString())
        String composerLockText = applicable.composerLock.getText(StandardCharsets.UTF_8.toString())

        def detectCodeLocation = packagistParser.getDependencyGraphFromProject(applicable.directoryString, composerJsonText, composerLockText)

        bomToolExtractionResultsFactory.fromCodeLocations([detectCodeLocation], getBomToolType(), applicable.directory)
    }
}
