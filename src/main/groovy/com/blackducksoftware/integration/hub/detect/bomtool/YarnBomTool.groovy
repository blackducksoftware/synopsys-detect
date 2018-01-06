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
package com.blackducksoftware.integration.hub.detect.bomtool

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnPackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class YarnBomTool extends BomTool {
    @Autowired
    YarnPackager yarnPackager

    @Autowired
    ExternalIdFactory externalIdFactory

    @Override
    public BomToolType getBomToolType() {
        BomToolType.YARN
    }

    @Override
    public boolean isBomToolApplicable() {
        detectFileManager.containsAllFiles(sourcePath, 'yarn.lock')
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        final File yarnLockFile = detectFileManager.findFile(sourceDirectory, 'yarn.lock')
        final List<String> yarnLockText = Files.readAllLines(yarnLockFile.toPath(), StandardCharsets.UTF_8)
        final DependencyGraph dependencyGraph = yarnPackager.parse(yarnLockText)
        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, sourcePath)
        final def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, externalId, dependencyGraph)

        return [detectCodeLocation]
    }
}
