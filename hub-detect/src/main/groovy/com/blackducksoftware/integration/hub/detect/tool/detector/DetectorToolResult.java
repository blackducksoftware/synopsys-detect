/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.tool.detector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.util.NameVersion;

public class DetectorToolResult {
    public Optional<NameVersion> bomToolProjectNameVersion;
    public List<DetectCodeLocation> bomToolCodeLocations;

    public Set<DetectorType> applicableDetectorTypes = new HashSet<>();
    public Set<DetectorType> failedDetectorTypes = new HashSet<>();
    public Set<DetectorType> succesfullDetectorTypes = new HashSet<>();

    public List<DetectorEvaluation> evaluatedDetectors = new ArrayList<>();

}
