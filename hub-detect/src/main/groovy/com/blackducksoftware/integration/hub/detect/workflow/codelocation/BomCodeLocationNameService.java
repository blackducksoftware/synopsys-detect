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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class BomCodeLocationNameService extends CodeLocationNameService {
    private final Logger logger = LoggerFactory.getLogger(BomCodeLocationNameService.class);

    public BomCodeLocationNameService(final DetectFileFinder detectFileFinder) {
        super(detectFileFinder);
    }

    public String createCodeLocationName(final String detectSourcePath, final String sourcePath, final ExternalId externalId, final BomToolGroupType bomToolType, final String prefix, final String suffix) {
        //path piece
        String relativePiece = sourcePath;
        try {
            final Path actualSourcePath = new File(sourcePath).toPath();
            final Path detectPath = new File(detectSourcePath).toPath();
            final Path detectParentPath = detectPath.getParent();
            final Path relativePath = detectParentPath.relativize(actualSourcePath);
            final List<String> relativePieces = new ArrayList<>();
            for (int i = 0; i < relativePath.getNameCount(); i++) {
                relativePieces.add(relativePath.getName(i).toFile().getName());
            }
            relativePiece = relativePieces.stream().collect(Collectors.joining("/"));
        } catch (final Exception e) {
            logger.info("Unable to relativize path, full source path will be used: " + sourcePath);
            logger.debug("The reason relativize failed: ", e);
        }
        //external id piece
        final List<String> pieces = Arrays.asList(externalId.getExternalIdPieces());
        final String externalIdPiece = pieces.stream().collect(Collectors.joining("/"));

        //misc pieces
        final String codeLocationTypeString = CodeLocationType.BOM.toString().toLowerCase();
        final String bomToolTypeString = bomToolType.toString().toLowerCase();

        String codeLocationName = createCommonName(relativePiece, externalIdPiece, prefix, suffix, codeLocationTypeString, bomToolTypeString);

        if (codeLocationName.length() > 250) {
            codeLocationName = shortenCodeLocationName(relativePiece, externalIdPiece, prefix, suffix, codeLocationTypeString, bomToolTypeString);
        }

        return codeLocationName;
    }

    private String createCommonName(final String pathPiece, final String externalIdPiece, final String prefix, final String suffix, final String codeLocationType, final String bomToolType) {
        String name = String.format("%s/%s", pathPiece, externalIdPiece);
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

    private String shortenCodeLocationName(final String pathPiece, final String externalIdPiece, final String prefix, final String suffix, final String codeLocationType, final String bomToolType) {
        final String shortenedPathPiece = shortenPiece(pathPiece);
        final String shortenedExternalIdPiece = shortenPiece(externalIdPiece);
        final String shortenedPrefix = shortenPiece(prefix);
        final String shortenedSuffix = shortenPiece(suffix);

        return createCommonName(shortenedPathPiece, shortenedExternalIdPiece, shortenedPrefix, shortenedSuffix, codeLocationType, bomToolType);
    }

}
