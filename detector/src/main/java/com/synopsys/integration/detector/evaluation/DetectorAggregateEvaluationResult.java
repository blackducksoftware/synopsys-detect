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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorAggregateEvaluationResult {
    private DetectorEvaluationTree evaluationTree;

    public DetectorAggregateEvaluationResult(DetectorEvaluationTree evaluationTree) {
        this.evaluationTree = evaluationTree;
    }

    public DetectorEvaluationTree getEvaluationTree() {
        return evaluationTree;
    }

    public List<DetectorEvaluation> getDetectorEvaluations() {
        return evaluationTree.getOrderedEvaluations();
    }

    public Set<DetectorType> getApplicableDetectorTypes() {
        return getDetectorEvaluations().stream()
                   .filter(DetectorEvaluation::isApplicable)
                   .map(DetectorEvaluation::getDetectorRule)
                   .map(DetectorRule::getDetectorType)
                   .collect(Collectors.toSet());
    }

    public Integer getExtractionCount() {
        return Math.toIntExact(getDetectorEvaluations().stream()
                                   .filter(DetectorEvaluation::isExtractable)
                                   .count());
    }
}
