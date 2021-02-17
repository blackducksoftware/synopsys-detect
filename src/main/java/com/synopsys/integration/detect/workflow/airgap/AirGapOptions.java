/*
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.airgap;

import java.nio.file.Path;
import java.util.Optional;

public class AirGapOptions {
    private final Path dockerInspectorPathOverride;
    private final Path gradleInspectorPathOverride;
    private final Path nugetInspectorPathOverride;

    public AirGapOptions(final Path dockerInspectorPathOverride, final Path gradleInspectorPathOverride, final Path nugetInspectorPathOverride) {
        this.dockerInspectorPathOverride = dockerInspectorPathOverride;
        this.gradleInspectorPathOverride = gradleInspectorPathOverride;
        this.nugetInspectorPathOverride = nugetInspectorPathOverride;
    }

    public Optional<Path> getDockerInspectorPathOverride() {
        return Optional.ofNullable(dockerInspectorPathOverride);
    }

    public Optional<Path> getGradleInspectorPathOverride() {
        return Optional.ofNullable(gradleInspectorPathOverride);
    }

    public Optional<Path> getNugetInspectorPathOverride() {
        return Optional.ofNullable(nugetInspectorPathOverride);
    }
}
