/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunResult;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;

public class ToolSupport {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventSystem eventSystem;
    private final DetectContext detectContext;
    private final SimpleToolDetector toolDetector;

    public ToolSupport(final EventSystem eventSystem, final DetectContext detectContext, final SimpleToolDetector toolDetector) {
        this.eventSystem = eventSystem;
        this.detectContext = detectContext;
        this.toolDetector = toolDetector;
    }

    public ToolResult run(final RunResult runResult) throws DetectorException {
        logger.info(String.format("Checking if %s applies.", toolDetector.getName()));
        DetectorResult applicableResult = toolDetector.applicable();
        if (applicableResult.getPassed()) {
            logger.info(String.format("Checking if %s is extractable.", toolDetector.getName()));
            DetectorResult extractableResult = toolDetector.extractable();
            if (extractableResult.getPassed()) {
                logger.info(String.format("Performing the %s extraction.", toolDetector.getName()));
                toolDetector.extract();
                return toolDetector.createToolResult(eventSystem, extractableResult, runResult);
            } else {
                logger.error(String.format("%s was not extractable.", toolDetector.getName()));
                logger.error(applicableResult.toDescription());
                eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
                return new DockerToolResult().failure(applicableResult.toDescription());
            }
        } else {
            logger.info("Docker was not applicable, will not actually run Docker tool.");
            logger.info(applicableResult.toDescription());
            return new DockerToolResult().skipped();
        }

        // TODO cleanup:
//        if (applicableResult.getPassed()) {
//            logger.info("Checking if Docker is extractable.");
//            DetectorResult extractableResult = dockerBomTool.extractable();
//            if (extractableResult.getPassed()) {
//                logger.info("Performing the Docker extraction.");
//                Extraction extractResult = dockerBomTool.extract();
//
//                DockerToolResult dockerToolResult = new DockerToolResult();
//                dockerToolResult.dockerCodeLocations = extractResult.codeLocations;
//                if (StringUtils.isNotBlank(extractResult.projectName) && StringUtils.isNotBlank(extractResult.projectVersion)) {
//                    dockerToolResult.dockerProjectNameVersion = Optional.of(new NameVersion(extractResult.projectName, extractResult.projectVersion));
//                }
//
//                Optional<Object> dockerTar = extractResult.getMetaDataValue(DockerExtractor.DOCKER_TAR_META_DATA_KEY);
//                if (dockerTar.isPresent()) {
//                    dockerToolResult.dockerTar = Optional.of((File) dockerTar.get());
//                }
//
//                if (extractResult.result == Extraction.ExtractionResultType.SUCCESS) {
//                    eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.SUCCESS));
//                } else {
//                    eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
//                }
//
//                return dockerToolResult;
//            } else {
//                logger.error("Docker was not extractable.");
//                logger.error(extractableResult.toDescription());
//                eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
//                return new DockerToolResult().failure(extractableResult.toDescription());
//            }
//        } else {
//            logger.info("Docker was not applicable, will not actually run Docker tool.");
//            logger.info(applicableResult.toDescription());
//            return new DockerToolResult().skipped();
//        }

    }
}
