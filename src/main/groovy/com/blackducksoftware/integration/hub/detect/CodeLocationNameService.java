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
package com.blackducksoftware.integration.hub.detect;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.CodeLocationName;
import com.blackducksoftware.integration.hub.detect.model.CodeLocationType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

@Component
/**
 * Since consistency of code location names is extremely important, this class will maintain all current and all historic ways of creating code location names.
 */
public class CodeLocationNameService {
    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private HubManager hubManager;

    public CodeLocationName createBomToolName(final String sourcePath, final String projectName, final String projectVersionName, final BomToolType bomToolType, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, bomToolType, sourcePath, null, prefix, suffix, CodeLocationType.BOM);
        return codeLocationName;
    }

    public CodeLocationName createScanName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, null, sourcePath, scanTargetPath, prefix, suffix, CodeLocationType.SCAN);
        return codeLocationName;
    }

    // used in 2.0.0
    public String generateBomToolVersion2(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final String version1 = generateBomToolVersion1(codeLocationName);
        hubManager.logCodeLocationNameExists(version1);

        final String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(codeLocationName.getSourcePath());
        final String projectName = codeLocationName.getProjectName();
        final String projectVersionName = codeLocationName.getProjectVersionName();
        final String prefix = codeLocationName.getPrefix();
        final String suffix = codeLocationName.getSuffix();
        final String codeLocationTypeString = codeLocationName.getCodeLocationType().toString().toLowerCase();
        final String bomToolTypeString = codeLocationName.getBomToolType() == null ? "" : codeLocationName.getBomToolType().toString();

        return createCommonVersion2Name(finalSourcePathPiece, projectName, projectVersionName, prefix, suffix, codeLocationTypeString, bomToolTypeString);
    }

    // used in 2.0.0
    public String generateScanVersion2(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final String version1 = generateScanVersion1(codeLocationName);
        hubManager.logCodeLocationNameExists(version1);

        final String cleanedTargetPath = cleanScanTargetPath(codeLocationName);
        final String projectName = codeLocationName.getProjectName();
        final String projectVersionName = codeLocationName.getProjectVersionName();
        final String prefix = codeLocationName.getPrefix();
        final String suffix = codeLocationName.getSuffix();
        final String codeLocationTypeString = codeLocationName.getCodeLocationType().toString().toLowerCase();
        final String bomToolTypeString = "";

        return createCommonVersion2Name(cleanedTargetPath, projectName, projectVersionName, prefix, suffix, codeLocationTypeString, bomToolTypeString);
    }

    // used in 0.0.7 to 1.2.0
    public String generateBomToolVersion1(final CodeLocationName codeLocationName) {
        final String projectName = codeLocationName.getProjectName();
        final String projectVersionName = codeLocationName.getProjectVersionName();
        final String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(codeLocationName.getSourcePath());
        final String bomToolString = codeLocationName.getBomToolType() == null ? "" : codeLocationName.getBomToolType().toString();
        final String prefix = codeLocationName.getPrefix();

        String name = String.format("%s/%s/%s/%s %s", bomToolString, finalSourcePathPiece, projectName, projectVersionName, "Hub Detect Tool");
        if (StringUtils.isNotBlank(prefix)) {
            name = String.format("%s/%s", prefix, name);
        }

        return name;
    }

    // used in 0.0.7 to 1.2.0
    public String generateScanVersion1(final CodeLocationName codeLocationName) {
        final String projectName = codeLocationName.getProjectName();
        final String projectVersionName = codeLocationName.getProjectVersionName();
        final String prefix = codeLocationName.getPrefix();
        final String cleanedTargetPath = cleanScanTargetPath(codeLocationName);

        String name = String.format("%s/%s/%s %s", cleanedTargetPath, projectName, projectVersionName, "Hub Detect Scan");
        if (StringUtils.isNotBlank(prefix)) {
            name = String.format("%s/%s", prefix, name);
        }

        return name;
    }

    private String cleanScanTargetPath(final CodeLocationName codeLocationName) {
        final String scanTargetPath = codeLocationName.getScanTargetPath();
        final String sourcePath = codeLocationName.getSourcePath();
        final String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(sourcePath);
        String cleanedTargetPath = "";
        if (StringUtils.isNotBlank(scanTargetPath) && StringUtils.isNotBlank(finalSourcePathPiece)) {
            cleanedTargetPath = scanTargetPath.replace(sourcePath, finalSourcePathPiece);
        }

        return cleanedTargetPath;
    }

    private String createCommonVersion2Name(final String pathPiece, final String projectName, final String projectVersionName, final String prefix, final String suffix, final String codeLocationType, final String bomToolType) {
        String name = String.format("%s/%s/%s", pathPiece, projectName, projectVersionName);
        if (StringUtils.isNotBlank(prefix)) {
            name = String.format("%s/%s", prefix, name);
        }
        if (StringUtils.isNotBlank(suffix)) {
            name = String.format("%s/%s", name, suffix);
        }

        String endPiece = codeLocationType;
        if (StringUtils.isNotBlank(bomToolType)) {
            endPiece = String.format("%s/%s", bomToolType, endPiece);
        }

        name = String.format("%s %s", name, endPiece);
        return name;
    }
}
