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
package com.blackducksoftware.integration.hub.detect.bomtool.output

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.util.IntegrationEscapeUtil

class DetectCodeLocation {
    private final BomToolType bomToolType
    private final String sourcePath
    private final String bomToolProjectName
    private final String bomToolProjectVersionName
    private final ExternalId bomToolProjectExternalId
    private final Set<DependencyNode> dependencies

    DetectCodeLocation(BomToolType bomToolType, String sourcePath, ExternalId bomToolProjectExternalId, Set<DependencyNode> dependencies) {
        this.bomToolType = bomToolType
        this.sourcePath = sourcePath
        this.bomToolProjectExternalId = bomToolProjectExternalId
        this.dependencies = dependencies
    }

    DetectCodeLocation(BomToolType bomToolType, String sourcePath, String bomToolProjectName, String bomToolProjectVersionName,
    ExternalId bomToolProjectExternalId, Set<DependencyNode> dependencies) {
        this.bomToolType = bomToolType
        this.sourcePath = sourcePath
        this.bomToolProjectName = bomToolProjectName
        this.bomToolProjectVersionName = bomToolProjectVersionName
        this.bomToolProjectExternalId = bomToolProjectExternalId
        this.dependencies = dependencies
    }

    DetectCodeLocation(final BomToolType bomToolType, final String sourcePath, final DependencyNode rootDependencyNode) {
        this.bomToolType = bomToolType
        this.sourcePath = sourcePath
        this.bomToolProjectName = rootDependencyNode.name
        this.bomToolProjectVersionName = rootDependencyNode.version
        this.bomToolProjectExternalId = rootDependencyNode.externalId
        this.dependencies = rootDependencyNode.children
    }

    public String createBdioFilename(IntegrationEscapeUtil integrationEscapeUtil, DetectFileManager detectFileManager, String projectName, String projectVersionName) {
        String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(sourcePath)
        List<String> safePieces = [
            bomToolType.toString(),
            projectName,
            projectVersionName,
            finalSourcePathPiece,
            'bdio'
        ].collect { integrationEscapeUtil.escapeForUri(it) }

        String filename = safePieces.join('_') + '.jsonld'
        filename
    }

    BomToolType getBomToolType() {
        bomToolType
    }

    String getSourcePath() {
        sourcePath
    }

    String getBomToolProjectName() {
        bomToolProjectName
    }

    String getBomToolProjectVersionName() {
        bomToolProjectVersionName
    }

    ExternalId getBomToolProjectExternalId() {
        bomToolProjectExternalId
    }

    Set<DependencyNode> getDependencies() {
        dependencies
    }
}
