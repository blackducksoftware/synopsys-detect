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
package com.synopsys.integration.detect.tool.detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.tool.detector.impl.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.project.DetectorEvaluationNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.DetectorNameVersionDecider;
import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.evaluation.DetectorEvaluator;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderDirectoryListException;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.util.NameVersion;
import java.io.File;
import java.util.stream.Collectors;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectContext detectContext;
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private DirectoryManager directoryManager;

    public DetectorTool(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DetectorToolResult performDetectors(File directory, DetectorFinderOptions detectorFinderOptions, String projectBomTool) throws DetectUserFriendlyException {
        logger.info("Initializing detector system.");

        directoryManager = detectContext.getBean(DirectoryManager.class);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);
        DetectableFactory detectableFactory = detectContext.getBean(DetectableFactory.class);
        DetectorFinder detectorFinder = new DetectorFinder();
        DetectorRuleFactory detectorRuleFactory = new DetectorRuleFactory();
        DetectorRuleSet detectRuleSet = detectorRuleFactory.createRules(detectableFactory, false);//TODO add the parse flag

        Optional<DetectorEvaluationTree> possibleRootEvaluation;
        try {
            logger.info("Starting detector file system traversal.");
            possibleRootEvaluation = detectorFinder.findDetectors(directory, detectRuleSet, detectorFinderOptions);

        } catch (DetectorFinderDirectoryListException e) {
            throw new DetectUserFriendlyException("Detect was unable to list a directory while searching for detectors.", e, ExitCodeType.FAILURE_DETECTOR);
        }

        if (!possibleRootEvaluation.isPresent()){
            return new DetectorToolResult();
        }

        DetectorEvaluationTree rootEvaluation = possibleRootEvaluation.get();
        List<DetectorEvaluation> detectorEvaluations = DetectorEvaluationUtils.flatten(rootEvaluation);

        logger.trace("Setting up detector events.");
        DetectorEventBroadcaster eventBroadcaster = new DetectorEventBroadcaster(eventSystem);
        DetectorEvaluator detectorEvaluator = new DetectorEvaluator();
        detectorEvaluator.setDetectorEventListener(eventBroadcaster);

        logger.info("Starting detector search.");
        detectorEvaluator.searchAndApplicableEvaluation(rootEvaluation, new HashSet<>());
        eventSystem.publishEvent(Event.SearchCompleted, rootEvaluation);

        logger.info("Starting detector preparation.");
        detectorEvaluator.extractableEvaluation(rootEvaluation);
        eventSystem.publishEvent(Event.PreparationsCompleted, rootEvaluation);

        logger.info("Starting detector extraction.");
        Integer extractionCount = Math.toIntExact(detectorEvaluations.stream()
                                  .filter(DetectorEvaluation::isExtractable)
                                  .count());
        eventSystem.publishEvent(Event.ExtractionCount, extractionCount);

        logger.info("Total number of extractions: " + extractionCount);

        detectorEvaluator.extractionEvaluation(rootEvaluation, this::createExtractionEnvironment);
        eventSystem.publishEvent(Event.ExtractionsCompleted, rootEvaluation);

        DetectorToolResult detectorToolResult = new DetectorToolResult();

        detectorToolResult.rootDetectorEvaluationTree = rootEvaluation;

        detectorToolResult.applicableDetectorTypes = detectorEvaluations.stream()
                                                         .filter(DetectorEvaluation::isApplicable)
                                                         .map(DetectorEvaluation::getDetectorRule)
                                                         .map(DetectorRule::getDetectorType)
                                                         .collect(Collectors.toSet());

        detectorToolResult.codeLocationMap = detectorEvaluations.stream()
                                                      .filter(DetectorEvaluation::wasExtractionSuccessful)
                                                      .map(it -> convert(directory, it))
                                                      .map(Map::entrySet)
                                                      .flatMap(Collection::stream)
                                                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        detectorToolResult.bomToolCodeLocations = new ArrayList<>(detectorToolResult.codeLocationMap.values());

        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        Optional<NameVersion> bomToolNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(detectorEvaluations, projectBomTool);
        detectorToolResult.bomToolProjectNameVersion = bomToolNameVersion;
        logger.info("Finished evaluating detectors for project info.");

        //Completed.
        logger.info("Finished running detectors.");
        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);

        return detectorToolResult;
    }

    int count = 0; //TODO, encapsulate this in something...
    private ExtractionEnvironment createExtractionEnvironment(DetectorEvaluation detectorEvaluation){
        ExtractionId extractionId = new ExtractionId(detectorEvaluation.getDetectorRule().getDetectorType(), count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }

    private Map<CodeLocation, DetectCodeLocation> convert(File detectSourcePath, DetectorEvaluation evaluation){
        Map<CodeLocation, DetectCodeLocation> detectCodeLocations = new HashMap<>();
        if (evaluation.wasExtractionSuccessful()){
            Extraction extraction = evaluation.getExtraction();
            for (CodeLocation codeLocation : extraction.getCodeLocations()){
                File sourcePath = codeLocation.getSourcePath().orElse(evaluation.getDetectableEnvironment().getDirectory());
                ExternalId externalId;
                if (!codeLocation.getExternalId().isPresent()){
                    logger.warn("The detector was unable to determine an external id for this code location, so an external id will be created using the file path.");
                    Forge detectForge = new Forge("/", "/", "Detect");
                    final String relativePath = FileNameUtils.relativize(detectSourcePath.getAbsolutePath(), sourcePath.getAbsolutePath());
                    externalId = externalIdFactory.createPathExternalId(detectForge, relativePath);
                    logger.warn("The external id that was created is: " + externalId.getExternalIdPieces().toString());
                } else {
                    externalId = codeLocation.getExternalId().get();
                }
                DetectCodeLocation detectCodeLocation = DetectCodeLocation.forDetector(codeLocation.getDependencyGraph(), sourcePath, externalId, evaluation.getDetectorRule().getDetectorType());
                detectCodeLocations.put(codeLocation, detectCodeLocation);
            }
        }
        return detectCodeLocations;
    }
}
