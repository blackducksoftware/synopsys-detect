/*
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
package com.synopsys.integration.detectable.detectable.codelocation;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class CodeLocation {
    private final File sourcePath;
    private final ExternalId externalId;
    private final DependencyGraph dependencyGraph;

    public CodeLocation(final DependencyGraph dependencyGraph) {
        this(dependencyGraph, null, null);
    }

    public CodeLocation(final DependencyGraph dependencyGraph, final File sourcePath) {
        this(dependencyGraph, null, sourcePath);
    }

    public CodeLocation(final DependencyGraph dependencyGraph, final ExternalId externalId) {
        this(dependencyGraph, externalId, null);
    }

    public CodeLocation(final DependencyGraph dependencyGraph, final ExternalId externalId, final File sourcePath) {
        this.sourcePath = sourcePath;
        this.externalId = externalId;
        this.dependencyGraph = dependencyGraph;
    }

    public Optional<File> getSourcePath() {
        return Optional.ofNullable(sourcePath);
    }

    public Optional<ExternalId> getExternalId() {
        return Optional.ofNullable(externalId);
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

}
