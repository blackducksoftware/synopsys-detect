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

import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunResult;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;

public class ToolRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventSystem eventSystem;
    private final SimpleToolDetector toolDetector;

    public ToolRunner(final EventSystem eventSystem, final SimpleToolDetector toolDetector) {
        this.eventSystem = eventSystem;
        this.toolDetector = toolDetector;
    }

    public void run(final RunResult runResult) throws DetectorException {
        logger.info(String.format("Checking if %s applies.", toolDetector.getName()));
        DetectorResult applicableResult = toolDetector.applicable();
        if (applicableResult.getPassed()) {
            logger.info(String.format("Checking if %s is extractable.", toolDetector.getName()));
            DetectorResult extractableResult = toolDetector.extractable();
            if (extractableResult.getPassed()) {
                logger.info(String.format("Performing the %s extraction.", toolDetector.getName()));
                toolDetector.extractAndPublishResults(eventSystem, runResult);
            } else {
                toolDetector.publishNotExtractableResults(eventSystem, extractableResult);
            }
        } else {
            logger.info("Docker was not applicable, will not actually run Docker tool.");
            logger.info(applicableResult.toDescription());
        }
    }
}
