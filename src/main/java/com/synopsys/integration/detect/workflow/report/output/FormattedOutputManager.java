/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.report.output;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.configuration.util.Bds;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class FormattedOutputManager {
    private DetectorToolResult detectorToolResult = null;
    private final List<Status> statusSummaries = new ArrayList<>();
    private final List<DetectResult> detectResults = new ArrayList<>();
    private final List<DetectIssue> detectIssues = new ArrayList<>();

    public FormattedOutputManager(final EventSystem eventSystem) {
        eventSystem.registerListener(Event.DetectorsComplete, this::detectorsComplete);
        eventSystem.registerListener(Event.StatusSummary, this::addStatusSummary);
        eventSystem.registerListener(Event.Issue, this::addIssue);
        eventSystem.registerListener(Event.ResultProduced, this::addDetectResult);
    }

    public FormattedOutput createFormattedOutput(DetectInfo detectInfo) {
        FormattedOutput formattedOutput = new FormattedOutput();
        formattedOutput.setFormatVersion("0.1.0");
        formattedOutput.setDetectVersion(detectInfo.getDetectVersion());

        formattedOutput.setResults(Bds.of(detectResults)
                                       .map(result -> new FormattedResultOutput(result.getResultLocation(), result.getResultMessage()))
                                       .toList());

        formattedOutput.setStatus(Bds.of(statusSummaries)
                                      .map(status -> new FormattedStatusOutput(status.getDescriptionKey(), status.getStatusType().toString()))
                                      .toList());

        formattedOutput.setIssues(Bds.of(detectIssues)
                                      .map(issue -> new FormattedIssueOutput(issue.getType().name(), issue.getMessages()))
                                      .toList());

        if (detectorToolResult != null) {
            formattedOutput.setDetectors(Bds.of(detectorToolResult.getRootDetectorEvaluationTree())
                                             .flatMap(DetectorEvaluationTree::allDescendentEvaluations)
                                             .filter(DetectorEvaluation::isApplicable)
                                             .map(this::convertDetector)
                                             .toList());
        }

        return formattedOutput;
    }

    private FormattedDetectorOutput convertDetector(DetectorEvaluation evaluation) {
        FormattedDetectorOutput detectorOutput = new FormattedDetectorOutput();
        detectorOutput.setFolder(evaluation.getDetectableEnvironment().getDirectory().toString());
        detectorOutput.setDescriptiveName(evaluation.getDetectorRule().getDescriptiveName());
        detectorOutput.setDetectorName(evaluation.getDetectorRule().getName());
        detectorOutput.setDetectorType(evaluation.getDetectorRule().getDetectorType().toString());

        detectorOutput.setSearchable(evaluation.isSearchable());
        detectorOutput.setApplicable(evaluation.isApplicable());
        detectorOutput.setExtractable(evaluation.isExtractable());
        detectorOutput.setExtracted(evaluation.wasExtractionSuccessful());
        detectorOutput.setDiscoverable(evaluation.wasDiscoverySuccessful());

        detectorOutput.setSearchableReason(evaluation.getSearchabilityMessage());
        detectorOutput.setApplicableReason(evaluation.getApplicabilityMessage());
        detectorOutput.setExtractableReason(evaluation.getExtractabilityMessage());
        if (evaluation.getDiscovery() != null) {
            detectorOutput.setDiscoveryReason(evaluation.getDiscovery().getDescription());
        }
        if (evaluation.getExtraction() != null) {
            detectorOutput.setExtractedReason(evaluation.getExtraction().getDescription());
            detectorOutput.setRelevantFiles(Bds.of(evaluation.getExtraction().getRelevantFiles()).map(File::toString).toList());
            detectorOutput.setProjectName(evaluation.getExtraction().getProjectName());
            detectorOutput.setProjectVersion(evaluation.getExtraction().getProjectVersion());
            if (evaluation.getExtraction().getCodeLocations() != null) {
                detectorOutput.setCodeLocationCount(evaluation.getExtraction().getCodeLocations().size());
            }
        }

        return detectorOutput;
    }

    private void detectorsComplete(final DetectorToolResult detectorToolResult) {
        this.detectorToolResult = detectorToolResult;
    }

    public void addStatusSummary(final Status status) {
        statusSummaries.add(status);
    }

    public void addIssue(DetectIssue issue) {
        detectIssues.add(issue);
    }

    public void addDetectResult(final DetectResult detectResult) {
        detectResults.add(detectResult);
    }

}
