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
package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExceptionDetectorResult;

public class PreparationManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventSystem eventSystem;

    public PreparationManager(final EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    private void prepare(final DetectorEvaluation result) {
        if (result.isApplicable()) {
            eventSystem.publishEvent(Event.ExtractableStarted, result.getDetector());
            try {
                result.setExtractable(result.getDetector().extractable());
            } catch (final Exception e) {
                logger.error("Detector " + result.getDetector().getDescriptiveName() + " was not extractable.", e);
                result.setExtractable(new ExceptionDetectorResult(e));
            }
            eventSystem.publishEvent(Event.ExtractableEnded, result.getDetector());
        }
    }

    public PreparationResult prepareExtractions(final List<DetectorEvaluation> results) {
        for (final DetectorEvaluation result : results) {
            prepare(result);
        }

        final Set<DetectorType> succesfulBomToolGroups = results.stream()
                                                             .filter(it -> it.isApplicable())
                                                             .filter(it -> it.isExtractable())
                                                             .map(it -> it.getDetector().getDetectorType())
                                                             .collect(Collectors.toSet());

        final Set<DetectorType> failedBomToolGroups = results.stream()
                                                          .filter(it -> it.isApplicable())
                                                          .filter(it -> !it.isExtractable())
                                                          .map(it -> it.getDetector().getDetectorType())
                                                          .collect(Collectors.toSet());

        return new PreparationResult(succesfulBomToolGroups, failedBomToolGroups, results);
    }
}
