/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;

public class ReplacedGradleGav implements GradleGavId {
    private final String group;
    private final String artifact;
    private final String version;

    public ReplacedGradleGav(String group, String artifact) {
        this(group, artifact, null);
    }

    public ReplacedGradleGav(String group, String artifact, @Nullable String version) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getArtifact() {
        return artifact;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    @Override
    public StringDependencyId toDependencyId() {
        String id = String.format("%s:%s%s", getGroup(), getArtifact(), getVersion()
                                                                            .map(version -> ":" + version)
                                                                            .orElse(""));
        return new StringDependencyId(id);
    }
}
