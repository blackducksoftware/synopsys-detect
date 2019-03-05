/**
 * detector
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
package com.synopsys.integration.detector.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.util.Set;

import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorEvaluationTree {
    //The following evaluation details are known when the evaluation is created.
    private File directory;
    private int depthFromRoot;
    private DetectorRuleSet detectorRuleSet;
    private List<DetectorEvaluation> orderedEvaluations;
    private Set<DetectorEvaluationTree> children;

    //The following are learned while being evaluated.
    private Set<DetectorType> appliedInParent = new HashSet<>();

    public DetectorEvaluationTree(final File directory, final int depthFromRoot, final DetectorRuleSet detectorRuleSet, final List<DetectorEvaluation> orderedEvaluations, final Set<DetectorEvaluationTree> children) {
        this.directory = directory;
        this.depthFromRoot = depthFromRoot;
        this.orderedEvaluations = orderedEvaluations;
        this.children = children;
        this.detectorRuleSet = detectorRuleSet;
    }

    public List<DetectorEvaluationTree> asFlatList(){
        List<DetectorEvaluationTree> evaluationTrees = new ArrayList<DetectorEvaluationTree>();
        evaluationTrees.add(this);
        for (DetectorEvaluationTree detectorEvaluationTree : children){
            evaluationTrees.addAll(detectorEvaluationTree.asFlatList());
        }
        return evaluationTrees;
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

    public Set<DetectorEvaluationTree> getChildren() {
        return children;
    }

    public Set<DetectorType> getAppliedInParent() {
        return appliedInParent;
    }

    public void setAppliedInParent(final Set<DetectorType> appliedInParent) {
        this.appliedInParent = appliedInParent;
    }

    public DetectorRuleSet getDetectorRuleSet() {
        return detectorRuleSet;
    }
}
