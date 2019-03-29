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
package com.synopsys.integration.detect.tool;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detect.workflow.project.DetectorProjectInfo;

public class DetectableToolResult {
    private Optional<File> dockerTar = Optional.empty();
    private final Optional<DetectToolProjectInfo> detectToolProjectInfo;
    private final List<DetectCodeLocation> detectCodeLocations;

    public DetectableToolResult(final Optional<DetectToolProjectInfo> detectToolProjectInfo, final List<DetectCodeLocation> detectCodeLocations, Optional<File> dockerTar) {
        this.detectToolProjectInfo = detectToolProjectInfo;
        this.detectCodeLocations = detectCodeLocations;
        this.dockerTar = dockerTar;
    }

    public static DetectableToolResult skip() {
        return new DetectableToolResult(Optional.empty(), Collections.emptyList(), Optional.empty());
    }

    public static DetectableToolResult complete(List<DetectCodeLocation> codeLocations, DetectToolProjectInfo projectInfo, File dockerTar) {
        return new DetectableToolResult(Optional.of(projectInfo), codeLocations, Optional.of(dockerTar));
    }

    public Optional<File> getDockerTar() {
        return dockerTar;
    }

    public Optional<DetectToolProjectInfo> getDetectToolProjectInfo() {
        return detectToolProjectInfo;
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }
}
