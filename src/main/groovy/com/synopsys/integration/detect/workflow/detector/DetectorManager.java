/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.extraction.ExtractionManager;
import com.synopsys.integration.detect.workflow.extraction.ExtractionResult;
import com.synopsys.integration.detect.workflow.extraction.PreparationManager;
import com.synopsys.integration.detect.workflow.extraction.PreparationResult;
import com.synopsys.integration.detect.workflow.search.SearchManager;
import com.synopsys.integration.detect.workflow.search.SearchResult;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class DetectorManager {

    SearchManager searchManager;
    PreparationManager preparationManager;
    ExtractionManager extractionManager;
    EventSystem eventSystem;

    public DetectorManager(SearchManager searchManager, ExtractionManager extractionManager, PreparationManager preparationManager, EventSystem eventSystem) {
        this.searchManager = searchManager;
        this.extractionManager = extractionManager;
        this.preparationManager = preparationManager;
        this.eventSystem = eventSystem;
    }

    public DetectorToolResult runDetectors() throws DetectUserFriendlyException {
        List<DetectorEvaluation> detectorEvaluations = new ArrayList<>();

        //search
        SearchResult searchResult = searchManager.performSearch();
        eventSystem.publishEvent(Event.SearchCompleted, searchResult);
        detectorEvaluations.addAll(searchResult.getDetectorEvaluations());

        //prepare
        PreparationResult preparationResult = preparationManager.prepareExtractions(detectorEvaluations);
        eventSystem.publishEvent(Event.PreparationsCompleted, preparationResult);

        //extract
        ExtractionResult extractionResult = extractionManager.performExtractions(detectorEvaluations);
        eventSystem.publishEvent(Event.ExtractionsCompleted, extractionResult);

        //create results
        DetectorToolResult detectorToolResult = new DetectorToolResult();
        detectorToolResult.evaluatedDetectors = detectorEvaluations;
        detectorToolResult.bomToolCodeLocations = extractionResult.getDetectCodeLocations();
        detectorToolResult.applicableDetectorTypes = searchResult.getApplicableBomTools();

        detectorToolResult.failedDetectorTypes.addAll(preparationResult.getFailedBomToolTypes());
        detectorToolResult.failedDetectorTypes.addAll(extractionResult.getFailedBomToolTypes());

        detectorToolResult.succesfullDetectorTypes.addAll(preparationResult.getSuccessfulBomToolTypes());
        detectorToolResult.succesfullDetectorTypes.addAll(extractionResult.getSuccessfulBomToolTypes());
        detectorToolResult.succesfullDetectorTypes.removeIf(it -> detectorToolResult.failedDetectorTypes.contains(it));

        //post status
        Map<DetectorType, StatusType> detectorStatus = new HashMap<>();
        detectorToolResult.succesfullDetectorTypes.forEach(it -> detectorStatus.put(it, StatusType.SUCCESS));
        detectorToolResult.failedDetectorTypes.forEach(it -> detectorStatus.put(it, StatusType.FAILURE));
        detectorStatus.forEach((detector, status) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(detector, status)));

        return detectorToolResult;
    }
}
