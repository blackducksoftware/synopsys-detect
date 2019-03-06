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
package com.synopsys.integration.detectable.detectables.gradle.inspection.inspector;

import java.util.Optional;

public class GradleInspectorScriptOptions {
    private final String excludedProjectNames;
    private final String includedProjectNames;
    private final String excludedConfigurationNames;
    private final String includedConfigurationNames;
    private final String gradleInspectorRepositoryUrl;
    private final String providedOnlineInspectorVersion;

    public GradleInspectorScriptOptions(final String excludedProjectNames, final String includedProjectNames, final String excludedConfigurationNames, final String includedConfigurationNames, final String gradleInspectorRepositoryUrl,
        final String providedOnlineInspectorVersion) {
        this.excludedProjectNames = excludedProjectNames;
        this.includedProjectNames = includedProjectNames;
        this.excludedConfigurationNames = excludedConfigurationNames;
        this.includedConfigurationNames = includedConfigurationNames;
        this.gradleInspectorRepositoryUrl = gradleInspectorRepositoryUrl;
        this.providedOnlineInspectorVersion = providedOnlineInspectorVersion;
    }

    public String getGradleInspectorRepositoryUrl() {
        return gradleInspectorRepositoryUrl;
    }

    public String getExcludedProjectNames() {
        return excludedProjectNames;
    }

    public String getIncludedProjectNames() {
        return includedProjectNames;
    }

    public String getExcludedConfigurationNames() {
        return excludedConfigurationNames;
    }

    public String getIncludedConfigurationNames() {
        return includedConfigurationNames;
    }

    public Optional<String> getProvidedOnlineInspectorVersion() {
        return Optional.ofNullable(providedOnlineInspectorVersion);
    }
}
