/**
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

import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class ProjectInfoGatherer {
    @Autowired
    DetectConfiguration detectConfiguration

    String getProjectName() {
        getProjectName(detectConfiguration.sourcePath)
    }

    //TODO: Change these methods to getProjectNameFromPath, these should be for special cases only
    //TODO: Instead, this class should be deleted and these methods should be moved in to DetectProjectManager
    String getProjectName(final String sourcePath) {
        getProjectName(sourcePath, null)
    }

    String getProjectName(final String sourcePath, final String defaultProjectName) {
        String projectName = defaultProjectName?.trim()

        if (detectConfiguration.getProjectName()) {
            projectName = detectConfiguration.getProjectName()
        } else if (!projectName && sourcePath) {
            String finalSourcePathPiece = extractFinalPieceFromSourcePath(sourcePath)
            projectName = finalSourcePathPiece
        }

        projectName
    }

    String getProjectVersionName() {
        getProjectVersionName(null)
    }

    String getProjectVersionName(final String defaultVersionName) {
        String projectVersion = defaultVersionName?.trim()

        if (detectConfiguration.getProjectVersionName()) {
            projectVersion = detectConfiguration.getProjectVersionName()
        } else if (!projectVersion) {
            projectVersion = DateTime.now().toString(detectConfiguration.getVersionTimeFormat())
        }

        projectVersion
    }

    String getCodeLocationName(final BomToolType bomToolType, final String sourcePath, final String projectName, final String projectVersion) {
        String codeLocation = detectConfiguration.getProjectCodeLocationName()
        if (!codeLocation?.trim()) {
            String finalSourcePathPiece = extractFinalPieceFromSourcePath(sourcePath)
            codeLocation = String.format('%s/%s/%s', finalSourcePathPiece, projectName, projectVersion)
        }
        return String.format('%s/%s Hub Detect Export', bomToolType.toString(), codeLocation)
    }

    private String extractFinalPieceFromSourcePath(String sourcePath) {
        if (sourcePath == null || sourcePath.length() == 0) {
            return ''
        }
        String normalizedSourcePath = FilenameUtils.normalizeNoEndSeparator(sourcePath, true)
        normalizedSourcePath[normalizedSourcePath.lastIndexOf('/') + 1..-1]
    }
}