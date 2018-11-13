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
package com.blackducksoftware.integration.hub.detect.tool.detector;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.DetectorFactory;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.detector.DetectorManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectorEvaluationNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectorNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.DetectorSearchProvider;
import com.synopsys.integration.util.NameVersion;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectContext detectContext;

    public DetectorTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DetectorToolResult performDetectors(SearchOptions searchOptions, String projectBomTool) {
        logger.info("Preparing to initialize detectors.");
        DetectorFactory detectorFactory = detectContext.getBean(DetectorFactory.class);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);

        logger.info("Building detector system.");
        DetectorSearchProvider detectorSearchProvider = new DetectorSearchProvider(detectorFactory);
        DetectorSearchEvaluator detectorSearchEvaluator = new DetectorSearchEvaluator();

        SearchManager searchManager = new SearchManager(searchOptions, detectorSearchProvider, detectorSearchEvaluator, eventSystem);
        PreparationManager preparationManager = new PreparationManager(eventSystem);
        ExtractionManager extractionManager = new ExtractionManager();

        DetectorManager detectorManager = new DetectorManager(searchManager, extractionManager, preparationManager, eventSystem);
        logger.info("Running detectors.");
        DetectorToolResult detectorToolResult = detectorManager.runDetectors();
        logger.info("Finished running detectors.");
        eventSystem.publishEvent(Event.BomToolsComplete, detectorToolResult);

        logger.info("Evaluating detectors for project info.");

        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        Optional<NameVersion> bomToolNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(detectorToolResult.evaluatedDetectors, projectBomTool);
        detectorToolResult.bomToolProjectNameVersion = bomToolNameVersion;
        logger.info("Finished evaluating detectors for project info.");

        return detectorToolResult;
    }
}
