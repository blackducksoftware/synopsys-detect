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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.github.zafarkhaja.semver.Version;

public class NugetInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorInstaller.class);

    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final NugetVersionResolver nugetVersionResolver;

    public NugetInspectorInstaller(final DetectConfiguration detectConfiguration, final ExecutableRunner executableRunner, final NugetVersionResolver nugetVersionResolver) {
        this.detectConfiguration = detectConfiguration;
        this.executableRunner = executableRunner;
        this.nugetVersionResolver = nugetVersionResolver;
    }

    public Optional<File> install(String inspectorName, String nugetExe, File outputDirectory) {
        try {
            Optional<Version> version = nugetVersionResolver.resolveInspectorVersion(nugetExe, inspectorName, shouldUseAirGap(), executableRunner);
            if (version.isPresent()) {
                return attemptInstalls(inspectorName, nugetExe, outputDirectory, version.get().toString());
            }
        } catch (ExecutableRunnerException | IOException | DetectUserFriendlyException e) {
            logger.info("ERROR");
        }
        return Optional.empty();
    }

    private Optional<File> attemptInstalls(String inspectorName, final String nugetExecutablePath, final File outputDirectory, final String inspectorVersion) throws IOException, ExecutableRunnerException {
        final String nugetInspectorName = inspectorName;

        if (shouldUseAirGap()) {
            final File airGapNugetInspectorDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH));
            logger.debug("Running in airgap mode. Resolving from local path");
            File installDirectory = new File(airGapNugetInspectorDirectory, "tools");
            return Optional.of(installDirectory);
        } else {
            logger.debug("Running online. Resolving through nuget");

            for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL)) {
                logger.debug("Attempting source: " + source);
                final boolean success = attemptInstallInspectorFromSource(source, inspectorName, nugetExecutablePath, outputDirectory, inspectorVersion);
                if (success) {
                    break;
                }
            }
            final String inspectorDirectoryName = nugetInspectorName + "." + inspectorVersion;
            final File installDirectory = new File(outputDirectory, inspectorDirectoryName);
            if (installDirectory.exists()) {
                return Optional.of(installDirectory);
            } else {
                logger.warn(String.format("Could not find the %s version: %s even after an install attempt.", nugetInspectorName, inspectorVersion));
                return Optional.empty();
            }
        }
    }

    private boolean attemptInstallInspectorFromSource(final String source, String inspectorName, final String nugetExecutablePath, final File outputDirectory, final String resolvedInspectorVersion)
        throws IOException, ExecutableRunnerException {
        final List<String> nugetOptions = new ArrayList<>(Arrays.asList(
            "install",
            inspectorName,
            "-OutputDirectory",
            outputDirectory.getCanonicalPath(),
            "-Source",
            source,
            "-Version",
            resolvedInspectorVersion)
        );

        final Optional<String> nugetConfigPath = detectConfiguration.getOptionalProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
        if (nugetConfigPath.isPresent()) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfigPath.get());
        }

        final Executable installInspectorExecutable = new Executable(new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)), nugetExecutablePath, nugetOptions);
        final ExecutableOutput result = executableRunner.execute(installInspectorExecutable);

        return result.getReturnCode() == 0 && StringUtils.isBlank(result.getErrorOutput());
    }

    private boolean shouldUseAirGap() {
        final Optional<String> airGapPath = detectConfiguration.getOptionalProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH);
        if (airGapPath.isPresent()) {
            final File airGapNugetInspectorDirectory = new File(airGapPath.get());
            return airGapNugetInspectorDirectory.exists();
        }

        return false;
    }
}
