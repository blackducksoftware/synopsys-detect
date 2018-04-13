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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.NestedBomTool
import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolSearchResult
import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolSearcher
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.nio.file.Files

@Component
@TypeChecked
class YarnBomTool extends BomTool implements NestedBomTool<BomToolSearchResult> {
    @Autowired
    YarnPackager yarnPackager

    @Autowired
    ExternalIdFactory externalIdFactory

    @Autowired
    YarnBomToolSearcher yarnBomToolSearcher

    private BomToolSearchResult searchResult;

    @Override
    public BomToolType getBomToolType() {
        BomToolType.YARN
    }

    @Override
    public boolean isBomToolApplicable() {
        BomToolSearchResult searchResult = bomToolSearcher.getBomToolSearchResult(sourcePath);
        if (searchResult.isApplicable()) {
            this.searchResult = searchResult;
            return true;
        }

        return false;
    }

    public List<DetectCodeLocation> extractDetectCodeLocations(BomToolSearchResult searchResult) {
        final File yarnLockFile = detectFileManager.findFile(searchResult.searchedDirectory, 'yarn.lock')
        final List<String> yarnLockText = Files.readAllLines(yarnLockFile.toPath(), StandardCharsets.UTF_8)
        final DependencyGraph dependencyGraph = yarnPackager.parse(yarnLockText)
        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, searchResult.searchedDirectory.canonicalPath)
        final def detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), searchResult.searchedDirectory.canonicalPath, externalId, dependencyGraph).build()

        return [detectCodeLocation]
    }

    public List<DetectCodeLocation> extractDetectCodeLocations() {
        return extractDetectCodeLocations(searchResult)
    }

    public BomToolSearcher getBomToolSearcher() {
        return yarnBomToolSearcher;
    }

    public Boolean canSearchWithinApplicableDirectory() {
        return false;
    }

}
