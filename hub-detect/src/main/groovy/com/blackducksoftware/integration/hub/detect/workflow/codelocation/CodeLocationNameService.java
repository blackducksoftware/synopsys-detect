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
package com.blackducksoftware.integration.hub.detect.workflow.codelocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;

public class CodeLocationNameService {
    private final Logger logger = LoggerFactory.getLogger(CodeLocationNameService.class);

    public static final int MAXIMUM_CODE_LOCATION_NAME_LENGTH = 250;
    private final DetectFileFinder detectFileFinder;

    public CodeLocationNameService(final DetectFileFinder detectFileFinder) {
        this.detectFileFinder = detectFileFinder;
    }

    public String createBomCodeLocationName(final String detectSourcePath, final String sourcePath, final ExternalId externalId, final BomToolGroupType bomToolType, final String prefix, final String suffix) {
        final String pathPiece = FileNameUtils.relativize(detectSourcePath, sourcePath);

        final List<String> pieces = Arrays.asList(externalId.getExternalIdPieces());
        final String externalIdPiece = StringUtils.join(pieces, "/");

        // misc pieces
        final String codeLocationTypeString = CodeLocationType.BOM.toString().toLowerCase();
        final String bomToolTypeString = bomToolType.toString().toLowerCase();

        final List<String> bomCodeLocationNamePieces = Arrays.asList(pathPiece, externalIdPiece);
        final List<String> bomCodeLocationEndPieces = Arrays.asList(bomToolTypeString, codeLocationTypeString);

        return createCodeLocationName(prefix, bomCodeLocationNamePieces, suffix, bomCodeLocationEndPieces);
    }

    public String createDockerCodeLocationName(final String sourcePath, final String projectName, final String projectVersionName, final String dockerImage, final BomToolGroupType bomToolType, final String prefix, final String suffix) {
        final String finalSourcePathPiece = detectFileFinder.extractFinalPieceFromPath(sourcePath);
        final String codeLocationTypeString = CodeLocationType.DOCKER.toString().toLowerCase();
        final String bomToolTypeString = bomToolType.toString().toLowerCase();

        final List<String> dockerCodeLocationNamePieces = Arrays.asList(finalSourcePathPiece, projectName, projectVersionName, dockerImage);
        final List<String> dockerCodeLocationEndPieces = Arrays.asList(codeLocationTypeString, bomToolTypeString);

        return createCodeLocationName(prefix, dockerCodeLocationNamePieces, suffix, dockerCodeLocationEndPieces);
    }

    public String createDockerScanCodeLocationName(final String dockerTarFilename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String codeLocationTypeString = CodeLocationType.SCAN.toString().toLowerCase();

        final List<String> fileCodeLocationNamePieces = Arrays.asList(dockerTarFilename, projectName, projectVersionName);
        final List<String> fileCodeLocationEndPieces = Arrays.asList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    public String createScanCodeLocationName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String pathPiece = cleanScanTargetPath(scanTargetPath, sourcePath);
        final String codeLocationTypeString = CodeLocationType.SCAN.toString().toLowerCase();

        final List<String> fileCodeLocationNamePieces = Arrays.asList(pathPiece, projectName, projectVersionName);
        final List<String> fileCodeLocationEndPieces = Arrays.asList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    public String createBinaryScanCodeLocationName(final String filename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String codeLocationTypeString = CodeLocationType.SCAN.toString().toLowerCase();

        final List<String> fileCodeLocationNamePieces = Arrays.asList(filename, projectName, projectVersionName);
        final List<String> fileCodeLocationEndPieces = Arrays.asList(codeLocationTypeString);

        return createCodeLocationName(prefix, fileCodeLocationNamePieces, suffix, fileCodeLocationEndPieces);
    }

    private String createCodeLocationName(final String prefix, final List<String> codeLocationNamePieces, final String suffix, final List<String> codeLocationEndPieces) {
        String codeLocationName = createCommonName(prefix, codeLocationNamePieces, suffix, codeLocationEndPieces);

        if (codeLocationName.length() > MAXIMUM_CODE_LOCATION_NAME_LENGTH) {
            codeLocationName = createShortenedCodeLocationName(codeLocationNamePieces, prefix, suffix, codeLocationEndPieces);
        }

        return codeLocationName;
    }

    private String cleanScanTargetPath(final String scanTargetPath, final String sourcePath) {
        final String finalSourcePathPiece = detectFileFinder.extractFinalPieceFromPath(sourcePath);
        String cleanedTargetPath = "";
        if (StringUtils.isNotBlank(scanTargetPath) && StringUtils.isNotBlank(finalSourcePathPiece)) {
            cleanedTargetPath = scanTargetPath.replace(sourcePath, finalSourcePathPiece);
        }

        return cleanedTargetPath;
    }

    private String createShortenedCodeLocationName(final List<String> namePieces, final String prefix, final String suffix, final List<String> endPieces) {
        final List<String> shortenedNamePieces = namePieces.stream().map(this::shortenPiece).collect(Collectors.toList());

        final String shortenedPrefix = shortenPiece(prefix);
        final String shortenedSuffix = shortenPiece(suffix);

        return createCommonName(shortenedPrefix, shortenedNamePieces, shortenedSuffix, endPieces);
    }

    private String shortenPiece(final String piece) {
        if (piece.length() <= 40) {
            return piece;
        } else {
            return piece.substring(0, 19) + "..." + piece.substring(piece.length() - 18);
        }
    }

    private String createCommonName(final String prefix, final List<String> namePieces, final String suffix, final List<String> endPieces) {
        final ArrayList<String> commonNamePieces = new ArrayList<>();

        if (StringUtils.isNotBlank(prefix)) {
            commonNamePieces.add(prefix);
        }

        commonNamePieces.addAll(namePieces);

        if (StringUtils.isNotBlank(suffix)) {
            commonNamePieces.add(suffix);
        }

        final String name = StringUtils.join(commonNamePieces, "/");
        final String endPiece = StringUtils.join(endPieces, "/");

        return String.format("%s %s", name, endPiece);
    }

}
