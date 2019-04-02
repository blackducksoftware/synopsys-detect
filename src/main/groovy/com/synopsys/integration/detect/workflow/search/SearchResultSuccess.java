/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.search;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;

public class SearchResultSuccess extends SearchResult {
    List<DetectorEvaluation> detectorEvaluations;
    Set<DetectorType> applicableBomTools;

    public SearchResultSuccess(final List<DetectorEvaluation> detectorEvaluations, Set<DetectorType> applicableBomTools) {
        this.detectorEvaluations = detectorEvaluations;
        this.applicableBomTools = applicableBomTools;
    }

    @Override
    public List<DetectorEvaluation> getDetectorEvaluations() {
        return detectorEvaluations;
    }

    @Override
    public Set<DetectorType> getApplicableBomTools() {
        return applicableBomTools;
    }

    @Override
    public boolean getSuccess() {
        return true;
    }

}
