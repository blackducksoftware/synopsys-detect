/**
 * detector
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
package com.synopsys.integration.detector.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluator extends Evaluator {
    private DetectorEvaluatorListener detectorEvaluatorListener;
    private ApplicableEvaluator applicableEvaluator;
    private ExtractableEvaluator extractableEvaluator;
    private DiscoveryEvaluator discoveryEvaluator;
    private ExtractionEvaluator extractionEvaluator;

    public DetectorEvaluator(DetectorEvaluationOptions evaluationOptions, Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider, DiscoveryFilter discoveryFilter) {
        super(evaluationOptions);
        applicableEvaluator = new ApplicableEvaluator(evaluationOptions);
        extractableEvaluator = new ExtractableEvaluator(evaluationOptions, extractionEnvironmentProvider);
        discoveryEvaluator = new DiscoveryEvaluator(evaluationOptions, discoveryFilter);
        extractionEvaluator = new ExtractionEvaluator(evaluationOptions);
    }

    @Override
    protected DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation) {
        DetectorEvaluationTree currentEvaluationTree = rootEvaluation;
        List<Evaluator> evaluators = createOrderedEvaluatorList();
        for (Evaluator evaluator : evaluators) {
            currentEvaluationTree = evaluator.evaluate(currentEvaluationTree).getEvaluationTree();
        }

        return currentEvaluationTree;
    }

    private List<Evaluator> createOrderedEvaluatorList() {
        List<Evaluator> evaluators = new ArrayList<>(4);
        evaluators.add(applicableEvaluator);
        evaluators.add(extractableEvaluator);
        evaluators.add(discoveryEvaluator);
        evaluators.add(extractionEvaluator);

        return evaluators;
    }

    public void registerPostApplicableCallback(Function<DetectorAggregateEvaluationResult, Void> callBack) {
        applicableEvaluator.registerEvaluatorResultCallback(callBack);
    }

    public void registerPostExtractableCallback(Function<DetectorAggregateEvaluationResult, Void> callBack) {
        extractableEvaluator.registerEvaluatorResultCallback(callBack);
    }

    public void registerPostDiscoveryCallback(Function<DetectorAggregateEvaluationResult, Void> callBack) {
        discoveryEvaluator.registerEvaluatorResultCallback(callBack);
    }

    public void registerPostExtractionCallback(Function<DetectorAggregateEvaluationResult, Void> callBack) {
        extractionEvaluator.registerEvaluatorResultCallback(callBack);
    }

    @Override
    public Optional<DetectorEvaluatorListener> getDetectorEvaluatorListener() {
        return Optional.ofNullable(detectorEvaluatorListener);
    }

    @Override
    public void setDetectorEvaluatorListener(DetectorEvaluatorListener detectorEvaluatorListener) {
        this.detectorEvaluatorListener = detectorEvaluatorListener;
        applicableEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        extractableEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        discoveryEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        extractionEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
    }
}
