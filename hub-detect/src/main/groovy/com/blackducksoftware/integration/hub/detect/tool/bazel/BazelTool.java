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
package com.blackducksoftware.integration.hub.detect.tool.bazel;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;
import com.synopsys.integration.util.NameVersion;

public class BazelTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectContext detectContext;

    public BazelTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public BazelToolResult run() {
        logger.info("Preparing the Bazel tool.");
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        DetectorEnvironment detectorEnvironment = new DetectorEnvironment(directoryManager.getSourceDirectory(), Collections.emptySet(), 0, null, false);
        BazelDetector bazelBomTool = detectContext.getBean(BazelDetector.class, detectorEnvironment);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);

        logger.info("Checking if Bazel applies.");
        DetectorResult applicableResult = bazelBomTool.applicable();
        if (applicableResult.getPassed()) {
            logger.info("Checking if Bazel is extractable.");
            DetectorResult extractableResult = bazelBomTool.extractable();
            if (extractableResult.getPassed()) {
                logger.info("Performing the Bazel extraction.");
                Extraction extractResult = bazelBomTool.extract();

                BazelToolResult bazelToolResult = new BazelToolResult();
                bazelToolResult.bazelCodeLocations = extractResult.codeLocations;
                if (StringUtils.isNotBlank(extractResult.projectName) && StringUtils.isNotBlank(extractResult.projectVersion)) {
                    bazelToolResult.bazelProjectNameVersion = Optional.of(new NameVersion(extractResult.projectName, extractResult.projectVersion));
                }

                if (extractResult.result == Extraction.ExtractionResultType.SUCCESS) {
                    eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.SUCCESS));
                } else {
                    eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
                }

                return bazelToolResult;
            } else {
                logger.error("Bazel was not extractable.");
                logger.error(extractableResult.toDescription());
                eventSystem.publishEvent(Event.StatusSummary, new Status(DetectTool.DOCKER.toString(), StatusType.FAILURE));
                return BazelToolResult.failure(extractableResult.toDescription());
            }
        } else {
            logger.info("Bazel was not applicable, will not actually run Bazel tool.");
            logger.info(applicableResult.toDescription());
            return BazelToolResult.skipped();
        }
    }
}
