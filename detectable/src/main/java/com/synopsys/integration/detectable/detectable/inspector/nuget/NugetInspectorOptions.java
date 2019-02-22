/**
 * detectable
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
package com.synopsys.integration.detectable.detectable.inspector.nuget;

import java.io.File;

public class NugetInspectorOptions {

    private final File targetDirectory;
    private final File outputDirectory;
    private final boolean ignoreFailures;
    private final String excludedModules;
    private final String includedModules;
    private final String[] packagesRepoUrl;
    private final String nugetConfigPath;

    public NugetInspectorOptions(final File targetDirectory, final File outputDirectory, final boolean ignoreFailures, final String excludedModules, final String includedModules, final String packagesRepoUrl[],
        final String nugetConfigPath) {
        this.targetDirectory = targetDirectory;
        this.outputDirectory = outputDirectory;
        this.ignoreFailures = ignoreFailures;
        this.excludedModules = excludedModules;
        this.includedModules = includedModules;
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetConfigPath = nugetConfigPath;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public boolean isIgnoreFailures() {
        return ignoreFailures;
    }

    public String getExcludedModules() {
        return excludedModules;
    }

    public String getIncludedModules() {
        return includedModules;
    }

    public String[] getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public String getNugetConfigPath() {
        return nugetConfigPath;
    }
}
