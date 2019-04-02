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
package com.synopsys.integration.detect.tool;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.docker.DockerExtractor;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.util.NameVersion;

public class ToolRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventSystem eventSystem;
    private final SimpleToolDetector toolDetector;

    public ToolRunner(final EventSystem eventSystem, final SimpleToolDetector toolDetector) {
        this.eventSystem = eventSystem;
        this.toolDetector = toolDetector;
    }

    public void run(final RunResult runResult) throws DetectorException {
        logger.info(String.format("Checking if %s applies.", toolDetector.getToolEnum().toString()));
        DetectorResult applicableResult = toolDetector.applicable();
        if (applicableResult.getPassed()) {
            logger.info(String.format("Checking if %s is extractable.", toolDetector.getToolEnum().toString()));
            DetectorResult extractableResult = toolDetector.extractable();
            if (extractableResult.getPassed()) {
                logger.info(String.format("Performing the %s extraction.", toolDetector.getToolEnum().toString()));
                Extraction extractionResults = toolDetector.extract();
                if (extractionResults.result != Extraction.ExtractionResultType.SUCCESS) {
                    logger.error(String.format("%s extraction failed: %s", toolDetector.getToolEnum().toString(), extractionResults.description));
                }
                publishExtractionResults(eventSystem, runResult, extractionResults);
            } else {
                publishNotExtractableResults(eventSystem, extractableResult, toolDetector.getToolEnum().toString());
            }
        } else {
            logger.info(String.format("%s was not applicable, will not actually run %s tool.", toolDetector.getToolEnum().toString(), toolDetector.getToolEnum().toString()));
            logger.info(applicableResult.toDescription());
        }
    }

    private void publishExtractionResults(final EventSystem eventSystem, final RunResult runResult, final Extraction extractionResult) {
        runResult.addToolNameVersionIfPresent(toolDetector.getToolEnum(), Optional.of(new NameVersion(extractionResult.projectName, extractionResult.projectVersion)));
        Optional<Object> dockerTar = extractionResult.getMetaDataValue(DockerExtractor.DOCKER_TAR_META_DATA_KEY);
        if (dockerTar.isPresent()) {
            runResult.addDockerFile(Optional.of((File) dockerTar.get()));
        }
        runResult.addDetectCodeLocations(extractionResult.codeLocations);
        if (extractionResult.result == Extraction.ExtractionResultType.SUCCESS) {
            eventSystem.publishEvent(Event.StatusSummary, new Status(toolDetector.getToolEnum().toString(), StatusType.SUCCESS));
        } else {
            eventSystem.publishEvent(Event.StatusSummary, new Status(toolDetector.getToolEnum().toString(), StatusType.FAILURE));
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_GENERAL_ERROR, extractionResult.description));
        }
    }

    private void publishNotExtractableResults(final EventSystem eventSystem, final DetectorResult extractableResult, final String toolName) {
        logger.error(String.format("%s was not extractable: %s", toolName, extractableResult.toDescription()));
        eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.BAZEL.toString(), StatusType.FAILURE));
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_GENERAL_ERROR, extractableResult.toDescription()));
    }
}
