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
package com.synopsys.integration.detect.tool.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorToolResult {
    private Optional<NameVersion> bomToolProjectNameVersion = Optional.empty();
    private List<DetectCodeLocation> bomToolCodeLocations = new ArrayList<>();

    private Set<DetectorType> applicableDetectorTypes = new HashSet<>();
    private Set<DetectorType> failedDetectorTypes = new HashSet<>();

    private Optional<DetectorEvaluationTree> rootDetectorEvaluationTree = Optional.empty();
    private Map<CodeLocation, DetectCodeLocation> codeLocationMap = new HashMap<>();

    public Optional<NameVersion> getBomToolProjectNameVersion() {
        return bomToolProjectNameVersion;
    }

    public void setBomToolProjectNameVersion(final Optional<NameVersion> bomToolProjectNameVersion) {
        this.bomToolProjectNameVersion = bomToolProjectNameVersion;
    }

    public List<DetectCodeLocation> getBomToolCodeLocations() {
        return bomToolCodeLocations;
    }

    public void setBomToolCodeLocations(final List<DetectCodeLocation> bomToolCodeLocations) {
        this.bomToolCodeLocations = bomToolCodeLocations;
    }

    public Set<DetectorType> getApplicableDetectorTypes() {
        return applicableDetectorTypes;
    }

    public void setApplicableDetectorTypes(final Set<DetectorType> applicableDetectorTypes) {
        this.applicableDetectorTypes = applicableDetectorTypes;
    }

    public Set<DetectorType> getFailedDetectorTypes() {
        return failedDetectorTypes;
    }

    public void setFailedDetectorTypes(final Set<DetectorType> failedDetectorTypes) {
        this.failedDetectorTypes = failedDetectorTypes;
    }

    public Optional<DetectorEvaluationTree> getRootDetectorEvaluationTree() {
        return rootDetectorEvaluationTree;
    }

    public void setRootDetectorEvaluationTree(final Optional<DetectorEvaluationTree> rootDetectorEvaluationTree) {
        this.rootDetectorEvaluationTree = rootDetectorEvaluationTree;
    }

    public Map<CodeLocation, DetectCodeLocation> getCodeLocationMap() {
        return codeLocationMap;
    }

    public void setCodeLocationMap(final Map<CodeLocation, DetectCodeLocation> codeLocationMap) {
        this.codeLocationMap = codeLocationMap;
    }
}
