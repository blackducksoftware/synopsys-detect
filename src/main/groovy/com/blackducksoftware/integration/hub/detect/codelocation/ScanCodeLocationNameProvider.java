/**
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
package com.blackducksoftware.integration.hub.detect.codelocation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ScanCodeLocationNameProvider extends CodeLocationNameProvider {
    public String generateName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String cleanedTargetPath = cleanScanTargetPath(scanTargetPath, sourcePath);
        final String codeLocationTypeString = CodeLocationType.SCAN.toString().toLowerCase();

        final String codeLocationNameString = createCommonName(cleanedTargetPath, projectName, projectVersionName, prefix, suffix, codeLocationTypeString);
        if (codeLocationNameString.length() > 250) {
            return shortenCodeLocationName(cleanedTargetPath, projectName, projectVersionName, prefix, suffix, codeLocationTypeString);
        } else {
            return codeLocationNameString;
        }
    }

    private String createCommonName(final String pathPiece, final String projectName, final String projectVersionName, final String prefix, final String suffix, final String codeLocationType) {
        String name = String.format("%s/%s/%s", pathPiece, projectName, projectVersionName);
        if (StringUtils.isNotBlank(prefix)) {
            name = String.format("%s/%s", prefix, name);
        }
        if (StringUtils.isNotBlank(suffix)) {
            name = String.format("%s/%s", name, suffix);
        }

        final String endPiece = codeLocationType;

        name = String.format("%s %s", name, endPiece);
        return name;
    }

    private String shortenCodeLocationName(final String pathPiece, final String projectName, final String projectVersionName, final String prefix, final String suffix, final String codeLocationType) {
        final String shortenedPathPiece = shortenPiece(pathPiece);
        final String shortenedProjectName = shortenPiece(projectName);
        final String shortenedProjectVersionName = shortenPiece(projectVersionName);
        final String shortenedPrefix = shortenPiece(prefix);
        final String shortenedSuffix = shortenPiece(suffix);

        return createCommonName(shortenedPathPiece, shortenedProjectName, shortenedProjectVersionName, shortenedPrefix, shortenedSuffix, codeLocationType);
    }

    private String cleanScanTargetPath(final String scanTargetPath, final String sourcePath) {
        final String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(sourcePath);
        String cleanedTargetPath = "";
        if (StringUtils.isNotBlank(scanTargetPath) && StringUtils.isNotBlank(finalSourcePathPiece)) {
            cleanedTargetPath = scanTargetPath.replace(sourcePath, finalSourcePathPiece);
        }

        return cleanedTargetPath;
    }

}
