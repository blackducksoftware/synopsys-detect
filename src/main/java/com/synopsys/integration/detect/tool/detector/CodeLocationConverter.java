/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class CodeLocationConverter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public CodeLocationConverter(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Map<CodeLocation, DetectCodeLocation> toDetectCodeLocation(final File detectSourcePath, final DetectorEvaluation evaluation) {
        final Map<CodeLocation, DetectCodeLocation> detectCodeLocations = new HashMap<>();
        if (evaluation.wasExtractionSuccessful()) {
            final Extraction extraction = evaluation.getExtraction();
            final String name = evaluation.getDetectorRule().getDetectorType().toString();
            return toDetectCodeLocation(detectSourcePath, extraction, evaluation.getDetectableEnvironment().getDirectory(), name);
        }
        return detectCodeLocations;
    }

    public Map<CodeLocation, DetectCodeLocation> toDetectCodeLocation(final File detectSourcePath, final Extraction extraction, final File overridePath, final String overrideName) {
        final Map<CodeLocation, DetectCodeLocation> detectCodeLocations = new HashMap<>();

        for (final CodeLocation codeLocation : extraction.getCodeLocations()) {
            final File sourcePath = codeLocation.getSourcePath().orElse(overridePath);
            final ExternalId externalId;
            if (!codeLocation.getExternalId().isPresent()) {
                logger.debug("The detector was unable to determine an external id for this code location, so an external id will be created using the file path.");
                final Forge detectForge = new Forge("/", "Detect");
                final String relativePath = FileNameUtils.relativize(detectSourcePath.getAbsolutePath(), sourcePath.getAbsolutePath());
                if (StringUtils.isNotBlank(relativePath)) {
                    externalId = externalIdFactory.createPathExternalId(detectForge, relativePath);
                } else {// Relativize from the parent.
                    externalId = externalIdFactory.createPathExternalId(detectForge, FileNameUtils.relativizeParent(detectSourcePath.getAbsolutePath(), sourcePath.getAbsolutePath()));
                }

                logger.debug("The external id that was created is: " + Arrays.asList(externalId.getExternalIdPieces()).toString());
            } else {
                externalId = codeLocation.getExternalId().get();
            }
            final Optional<String> dockerImageName = extraction.getMetaData(DockerExtractor.DOCKER_IMAGE_NAME_META_DATA);
            final DetectCodeLocation detectCodeLocation;
            if (dockerImageName.isPresent()) {
                detectCodeLocation = DetectCodeLocation.forDocker(codeLocation.getDependencyGraph(), sourcePath, externalId, dockerImageName.get());
            } else {
                detectCodeLocation = DetectCodeLocation.forCreator(codeLocation.getDependencyGraph(), sourcePath, externalId, overrideName);
            }

            detectCodeLocations.put(codeLocation, detectCodeLocation);
        }

        return detectCodeLocations;
    }
}
