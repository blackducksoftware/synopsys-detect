/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.ExceptionUtil;
import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorIssuePublisher {

    public void publishEvents(final EventSystem eventSystem, final DetectorEvaluationTree rootEvaluationTree) {
        publishEvents(eventSystem, rootEvaluationTree.asFlatList());
    }

    private void publishEvents(final EventSystem eventSystem, final List<DetectorEvaluationTree> trees) {
        final String spacer = "\t\t";
        for (final DetectorEvaluationTree tree : trees) {
            final List<DetectorEvaluation> excepted = DetectorEvaluationUtils.filteredChildren(tree, DetectorEvaluation::wasExtractionException);
            final List<DetectorEvaluation> failed = DetectorEvaluationUtils.filteredChildren(tree, DetectorEvaluation::wasExtractionFailure);
            final List<DetectorEvaluation> notExtractable = DetectorEvaluationUtils.filteredChildren(tree, evaluation -> evaluation.isApplicable() && !evaluation.isExtractable());
            final List<DetectorEvaluation> extractableFailed = notExtractable.stream().filter(it -> !it.isFallbackExtractable() && !it.isPreviousExtractable()).collect(Collectors.toList());
            //For now, log only ones that used fallback.
            final List<DetectorEvaluation> extractableFailedButFallback = notExtractable.stream().filter(DetectorEvaluation::isFallbackExtractable).collect(Collectors.toList());
            //List<DetectorEvaluation> extractable_failed_but_skipped = notExtractable.stream().filter(it -> it.isPreviousExtractable()).collect(Collectors.toList());

            final List<String> messages = new ArrayList<>();

            addFallbackIfNotEmpty(messages, "\tUsed Fallback: ", spacer, extractableFailedButFallback, DetectorEvaluation::getExtractabilityMessage);
            //writeEvaluationsIfNotEmpty(writer, "\tSkipped: ", spacer, extractable_failed_but_skipped, DetectorEvaluation::getExtractabilityMessage);
            addIfNotEmpty(messages, "\tNot Extractable: ", spacer, extractableFailed, DetectorEvaluation::getExtractabilityMessage);
            addIfNotEmpty(messages, "\tFailure: ", spacer, failed, detectorEvaluation -> detectorEvaluation.getExtraction().getDescription());
            addIfNotEmpty(messages, "\tException: ", spacer, excepted, detectorEvaluation -> ExceptionUtil.oneSentenceDescription(detectorEvaluation.getExtraction().getError()));

            if (messages.size() > 0) {
                messages.add(0, tree.getDirectory().toString());
                eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.DETECTOR, messages));
            }
        }
    }

    private void addIfNotEmpty(final List<String> messages, final String prefix, final String spacer, final List<DetectorEvaluation> evaluations, final Function<DetectorEvaluation, String> reason) {
        if (evaluations.size() > 0) {
            evaluations.forEach(evaluation -> {
                messages.add(prefix + evaluation.getDetectorRule().getDescriptiveName());
                messages.add(spacer + reason.apply(evaluation));
            });
        }
    }

    private void addFallbackIfNotEmpty(final List<String> messages, final String prefix, final String spacer, final List<DetectorEvaluation> evaluations, final Function<DetectorEvaluation, String> reason) {
        if (evaluations.size() > 0) {
            evaluations.forEach(evaluation -> {
                final Optional<DetectorEvaluation> fallback = evaluation.getSuccessfulFallback();
                fallback.ifPresent(detectorEvaluation -> {
                    messages.add(prefix + detectorEvaluation.getDetectorRule().getDescriptiveName());
                    messages.add(spacer + "Preferred Detector: " + evaluation.getDetectorRule().getDescriptiveName());
                    messages.add(spacer + "Reason: " + reason.apply(evaluation));
                });

            });
        }
    }

}
