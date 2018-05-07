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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class YarnBomTool extends BomTool<YarnApplicableResult> {
    @Autowired
    YarnPackager yarnPackager

    @Override
    public BomToolType getBomToolType() {
        BomToolType.YARN
    }

    @Override
    public YarnApplicableResult isBomToolApplicable(File directory) {
        final File yarnLockFile = detectFileFinder.findFile(directory, 'yarn.lock')

        if (yarnLockFile) {
            return new YarnApplicableResult(directory, yarnLockFile);
        }

        return null;
    }

    public BomToolExtractionResult extractDetectCodeLocations(YarnApplicableResult applicable) {

        final List<String> yarnLockText = Files.readAllLines(applicable.yarnLock.toPath(), StandardCharsets.UTF_8)
        final DependencyGraph dependencyGraph = yarnPackager.parse(yarnLockText)
        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, applicable.directory.canonicalPath)
        final def detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), applicable.directory.canonicalPath, externalId, dependencyGraph).build()

        bomToolExtractionResultsFactory.fromCodeLocations([detectCodeLocation], getBomToolType(), applicable.directory);
    }

}
