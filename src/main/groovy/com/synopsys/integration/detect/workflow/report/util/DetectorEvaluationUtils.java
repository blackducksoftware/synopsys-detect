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
package com.synopsys.integration.detect.workflow.report.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluationUtils {

    public static List<DetectorEvaluation> applicableChildren(DetectorEvaluationTree tree){
        return filteredChildren(tree, DetectorEvaluation::isApplicable);
    }

    public static List<DetectorEvaluation> applicableDescendants(DetectorEvaluationTree tree){
        return filteredDescendents(tree, DetectorEvaluation::isApplicable);
    }

    public static List<DetectorEvaluation> notApplicableChildren(DetectorEvaluationTree tree){
        return filteredChildren(tree, detectorEvaluation -> !detectorEvaluation.isApplicable());
    }

    public static List<DetectorEvaluation> notSearchableChildren(DetectorEvaluationTree tree){
        return filteredChildren(tree, detectorEvaluation -> !detectorEvaluation.isSearchable());
    }

    public static List<DetectorEvaluation> extractionSuccessDescendents(DetectorEvaluationTree tree){
        return filteredDescendents(tree, DetectorEvaluation::wasExtractionSuccessful);
    }

    public static List<DetectorEvaluation> filteredChildren(DetectorEvaluationTree tree, Predicate<DetectorEvaluation> predicate){
        return tree.getOrderedEvaluations().stream().filter(predicate).collect(Collectors.toList());
    }

    public static List<DetectorEvaluation> filteredDescendents(DetectorEvaluationTree tree, Predicate<DetectorEvaluation> predicate){
        return tree.allDescendentEvaluations().stream().filter(predicate).collect(Collectors.toList());
    }
}
