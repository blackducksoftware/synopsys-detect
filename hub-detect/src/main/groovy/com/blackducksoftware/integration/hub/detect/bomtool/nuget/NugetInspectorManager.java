/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.github.zafarkhaja.semver.Version;

public class NugetInspectorManager {
    private final NugetInspectorVersionResolver nugetInspectorVersionResolver;
    private final NugetInspectorInstaller nugetInspectorInstaller;
    private final ExecutableManager executableManager;
    private final DetectConfiguration detectConfiguration;

    private String resolvedNugetInspectorExecutable = null;

    public NugetInspectorManager(final NugetInspectorVersionResolver nugetInspectorVersionResolver, final NugetInspectorInstaller nugetInspectorInstaller, final ExecutableManager executableManager,
        final DetectConfiguration detectConfiguration) {
        this.nugetInspectorVersionResolver = nugetInspectorVersionResolver;
        this.nugetInspectorInstaller = nugetInspectorInstaller;
        this.executableManager = executableManager;
        this.detectConfiguration = detectConfiguration;
    }

    public String findNugetInspector() throws BomToolException {
        if (resolvedNugetInspectorExecutable == null) {
            try {
                final String detectSourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH);
                final String nugetPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_PATH);
                final String nugetExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.NUGET, true, new File(detectSourcePath), nugetPath);
                final Optional<Version> nugetInspectorVersion = nugetInspectorVersionResolver.resolveInspectorVersion(nugetExecutablePath);

                if (nugetInspectorVersion.isPresent()) {
                    resolvedNugetInspectorExecutable = nugetInspectorInstaller.install(nugetInspectorVersion.get().toString(), nugetExecutablePath);
                }
            } catch (final DetectUserFriendlyException | ExecutableRunnerException | IOException e) {
                throw new BomToolException(e);
            }
        }

        return resolvedNugetInspectorExecutable;
    }

}
