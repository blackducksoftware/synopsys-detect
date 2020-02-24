/**
 * detectable
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
package com.synopsys.integration.detectable.detectable.inspector.nuget;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class NugetInspectorOptions {
    private final boolean ignoreFailures;
    private final String excludedModules;
    private final String includedModules;
    private final List<String> packagesRepoUrl;
    private final Path nugetConfigPath;

    public NugetInspectorOptions(final boolean ignoreFailures, final String excludedModules, final String includedModules, final List<String> packagesRepoUrl, final Path nugetConfigPath) {
        this.ignoreFailures = ignoreFailures;
        this.excludedModules = excludedModules;
        this.includedModules = includedModules;
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetConfigPath = nugetConfigPath;
    }

    public boolean isIgnoreFailures() {
        return ignoreFailures;
    }

    public Optional<String> getExcludedModules() {
        return Optional.ofNullable(excludedModules);
    }

    public Optional<String> getIncludedModules() {
        return Optional.ofNullable(includedModules);
    }

    public List<String> getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public Optional<Path> getNugetConfigPath() {
        return Optional.ofNullable(nugetConfigPath);
    }
}
