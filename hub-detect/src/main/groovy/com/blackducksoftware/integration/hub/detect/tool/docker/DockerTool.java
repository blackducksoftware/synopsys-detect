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
package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;

public class DockerTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectContext detectContext;

    public DockerTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DockerToolResult run() throws DetectorException {
        logger.info("Preparing to run Docker.");
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        DetectorEnvironment detectorEnvironment = new DetectorEnvironment(directoryManager.getSourceDirectory(), Collections.emptySet(), 0, null, false);
        DockerDetector dockerBomTool = detectContext.getBean(DockerDetector.class, detectorEnvironment);

        logger.info("Checking it applies.");
        DetectorResult applicableResult = dockerBomTool.applicable();
        if (applicableResult.getPassed()) {
            logger.info("Checking it is extractable.");
            DetectorResult extractableResult = dockerBomTool.extractable();
            if (extractableResult.getPassed()) {
                logger.info("Performing the extraction.");
                ExtractionId extractionId = new ExtractionId(DetectCodeLocationType.DOCKER.toString(), "docker");
                Extraction extractResult = dockerBomTool.extract(extractionId);

                DockerToolResult dockerToolResult = new DockerToolResult();
                dockerToolResult.dockerCodeLocations = extractResult.codeLocations;

                Optional<Object> dockerTar = extractResult.getMetaDataValue(DockerExtractor.DOCKER_TAR_META_DATA_KEY);
                if (dockerTar.isPresent()) {
                    dockerToolResult.dockerTar = Optional.of((File) dockerTar.get());
                }

                return dockerToolResult;
            } else {
                logger.error("Docker was not extractable even though the tool attempted to run.");
                logger.error(applicableResult.toDescription());
            }
        } else {
            logger.error("Docker was not applicable even though the tool attempted to run.");
            logger.error(applicableResult.toDescription());
        }

        return new DockerToolResult();
    }
}
