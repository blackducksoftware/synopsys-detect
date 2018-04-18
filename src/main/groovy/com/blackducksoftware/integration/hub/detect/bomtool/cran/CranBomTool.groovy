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
package com.blackducksoftware.integration.hub.detect.bomtool.cran

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
class CranBomTool extends BomTool<CranApplicableResult> {
    @Autowired
    PackratPackager packratPackager

    BomToolType getBomToolType() {
        return BomToolType.CRAN
    }

    CranApplicableResult isBomToolApplicable(File directory) {
        List<File> packratLockFiles = detectFileManager.findFilesToDepth(directory, 'packrat.lock', detectConfiguration.getSearchDepth());
        if (packratLockFiles.size() > 0) {
            return new CranApplicableResult(directory, packratLockFiles);
        }
        return null;
    }

    BomToolExtractionResult extractDetectCodeLocations(CranApplicableResult applicable) {
        File sourceDirectory = detectConfiguration.sourceDirectory

        String projectName = ''
        String projectVersion = ''
        if (detectFileManager.containsAllFiles(applicable.directory,'DESCRIPTION')) {
            def descriptionFile = new File(applicable.directory, 'DESCRIPTION')
            List<String> descriptionText = Files.readAllLines(descriptionFile.toPath(), StandardCharsets.UTF_8)
            projectName = packratPackager.getProjectName(descriptionText)
            projectVersion = packratPackager.getVersion(descriptionText)
        }

        File packratLockFile = applicable.packratLockFiles.first();

        List<String> packratLockText = Files.readAllLines(packratLockFile.toPath(), StandardCharsets.UTF_8)
        DependencyGraph dependencyGraph = packratPackager.extractProjectDependencies(packratLockText)
        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CRAN, applicable.directory.toString())

        DetectCodeLocation.Builder builder =
                new DetectCodeLocation.Builder(getBomToolType(), applicable.directory.toString(), externalId, dependencyGraph)
                .bomToolProjectName(projectName)
                .bomToolProjectVersionName(projectVersion);

        def codeLocation = builder.build()
        bomToolExtractionResultsFactory.fromCodeLocations([codeLocation], getBomToolType(), applicable.directory);
    }
}
