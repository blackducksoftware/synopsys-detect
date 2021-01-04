/**
 * detector
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
package com.synopsys.integration.detector.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorEvaluationTree {
    //The following evaluation details are known when the evaluation is created.
    private final File directory;
    private final int depthFromRoot;
    private final DetectorRuleSet detectorRuleSet;
    private final List<DetectorEvaluation> orderedEvaluations;
    private final Set<DetectorEvaluationTree> children;

    public DetectorEvaluationTree(final File directory, final int depthFromRoot, final DetectorRuleSet detectorRuleSet, final List<DetectorEvaluation> orderedEvaluations, final Set<DetectorEvaluationTree> children) {
        this.directory = directory;
        this.depthFromRoot = depthFromRoot;
        this.orderedEvaluations = orderedEvaluations;
        this.children = children;
        this.detectorRuleSet = detectorRuleSet;
    }

    public List<DetectorEvaluationTree> asFlatList() {
        final List<DetectorEvaluationTree> evaluationTrees = new ArrayList<>();
        evaluationTrees.add(this);
        for (final DetectorEvaluationTree detectorEvaluationTree : children) {
            evaluationTrees.addAll(detectorEvaluationTree.asFlatList());
        }
        return evaluationTrees;
    }

    public List<DetectorEvaluation> allDescendentEvaluations() {
        return asFlatList()
                   .stream()
                   .flatMap(it -> it.getOrderedEvaluations().stream())
                   .collect(Collectors.toList());
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepthFromRoot() {
        return depthFromRoot;
    }

    public List<DetectorEvaluation> getOrderedEvaluations() {
        return orderedEvaluations;
    }

    public Optional<DetectorEvaluation> getEvaluation(final DetectorRule rule) {
        return orderedEvaluations.stream()
                   .filter(detectorEvaluation1 -> detectorEvaluation1.getDetectorRule().equals(rule))
                   .findFirst();
    }

    public Set<DetectorEvaluationTree> getChildren() {
        return children;
    }

    public DetectorRuleSet getDetectorRuleSet() {
        return detectorRuleSet;
    }
}
