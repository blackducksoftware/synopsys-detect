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
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.util.List;
import java.util.Optional;

public class MavenCliExtractorOptions {
    private final String mavenBuildCommand;
    private final List<String> mavenExcludedScopes;
    private final List<String> mavenIncludedScopes;
    private final List<String> mavenExcludedModules;
    private final List<String> mavenIncludedModules;

    public MavenCliExtractorOptions(String mavenBuildCommand, List<String> mavenExcludedScopes, List<String> mavenIncludedScopes, List<String> mavenExcludedModules, List<String> mavenIncludedModules) {
        this.mavenBuildCommand = mavenBuildCommand;
        this.mavenExcludedScopes = mavenExcludedScopes;
        this.mavenIncludedScopes = mavenIncludedScopes;
        this.mavenExcludedModules = mavenExcludedModules;
        this.mavenIncludedModules = mavenIncludedModules;
    }

    public Optional<String> getMavenBuildCommand() {
        return Optional.ofNullable(mavenBuildCommand);
    }

    public List<String> getMavenExcludedScopes() {
        return mavenExcludedScopes;
    }

    public List<String> getMavenIncludedScopes() {
        return mavenIncludedScopes;
    }

    public List<String> getMavenExcludedModules() {
        return mavenExcludedModules;
    }

    public List<String> getMavenIncludedModules() {
        return mavenIncludedModules;
    }
}
