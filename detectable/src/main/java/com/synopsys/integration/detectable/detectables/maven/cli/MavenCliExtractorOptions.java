/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.maven.cli;

public class MavenCliExtractorOptions {
    private final String mavenBuildCommand;
    private final String mavenScope;
    private final String mavenExcludedModules;
    private final String mavenIncludedModules;

    public MavenCliExtractorOptions(final String mavenBuildCommand, final String mavenScope, final String mavenExcludedModules, final String mavenIncludedModules) {
        this.mavenBuildCommand = mavenBuildCommand;
        this.mavenScope = mavenScope;
        this.mavenExcludedModules = mavenExcludedModules;
        this.mavenIncludedModules = mavenIncludedModules;
    }

    public String getMavenBuildCommand() {
        return mavenBuildCommand;
    }

    public String getMavenScope() {
        return mavenScope;
    }

    public String getMavenExcludedModules() {
        return mavenExcludedModules;
    }

    public String getMavenIncludedModules() {
        return mavenIncludedModules;
    }
}
