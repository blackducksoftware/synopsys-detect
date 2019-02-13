/**
 * detect-application
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
package com.synopsys.integration.detect.workflow.extraction;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;

public class PreparationResult {
    private final Set<DetectorType> failedBomToolGroups;
    private final Set<DetectorType> successfulBomToolGroups;
    private List<DetectorEvaluation> detectorEvaluations;

    public PreparationResult(final Set<DetectorType> successfulBomToolGroups, final Set<DetectorType> failedBomToolGroups,
        final List<DetectorEvaluation> detectorEvaluations) {
        this.failedBomToolGroups = failedBomToolGroups;
        this.successfulBomToolGroups = successfulBomToolGroups;
        this.detectorEvaluations = detectorEvaluations;
    }

    public boolean getSuccess() {
        return true;
    }

    public Set<DetectorType> getSuccessfulBomToolTypes() {
        return successfulBomToolGroups;
    }

    public Set<DetectorType> getFailedBomToolTypes() {
        return failedBomToolGroups;
    }

    public List<DetectorEvaluation> getDetectorEvaluations() {
        return detectorEvaluations;
    }
}
