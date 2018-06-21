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

import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;

@Component
public class DockerCodeLocationNameService extends CodeLocationNameService {
    public String createCodeLocationName(final String sourcePath, final String projectName, final String projectVersionName, final String dockerImage, final BomToolGroupType bomToolType, final String prefix, final String suffix) {
        final String finalSourcePathPiece = detectFileFinder.extractFinalPieceFromPath(sourcePath);
        final String codeLocationTypeString = CodeLocationType.DOCKER.toString().toLowerCase();
        final String bomToolTypeString = bomToolType.toString().toLowerCase();

        String codeLocationName = createCommonName(finalSourcePathPiece, projectName, projectVersionName, dockerImage, prefix, suffix, codeLocationTypeString, bomToolTypeString);

        if (codeLocationName.length() > 250) {
            codeLocationName = shortenCodeLocationName(finalSourcePathPiece, projectName, projectVersionName, dockerImage, prefix, suffix, codeLocationTypeString, bomToolTypeString);
        }

        return codeLocationName;
    }

    private String createCommonName(final String pathPiece, final String projectName, final String projectVersionName, final String dockerImage, final String prefix, final String suffix, final String codeLocationType,
            final String bomToolType) {
        String name = String.format("%s/%s/%s/%s", pathPiece, projectName, projectVersionName, dockerImage);
        if (StringUtils.isNotBlank(prefix)) {
            name = String.format("%s/%s", prefix, name);
        }
        if (StringUtils.isNotBlank(suffix)) {
            name = String.format("%s/%s", name, suffix);
        }

        String endPiece = codeLocationType;
        endPiece = String.format("%s/%s", bomToolType, endPiece);

        name = String.format("%s %s", name, endPiece);
        return name;
    }

    private String shortenCodeLocationName(final String pathPiece, final String projectName, final String projectVersionName, final String dockerImage, final String prefix, final String suffix, final String codeLocationType,
            final String bomToolType) {
        final String shortenedPathPiece = shortenPiece(pathPiece);
        final String shortenedProjectName = shortenPiece(projectName);
        final String shortenedProjectVersionName = shortenPiece(projectVersionName);
        final String shortenedDockerImage = shortenPiece(dockerImage);
        final String shortenedPrefix = shortenPiece(prefix);
        final String shortenedSuffix = shortenPiece(suffix);

        return createCommonName(shortenedPathPiece, shortenedProjectName, shortenedProjectVersionName, shortenedDockerImage, shortenedPrefix, shortenedSuffix, codeLocationType, bomToolType);
    }

}