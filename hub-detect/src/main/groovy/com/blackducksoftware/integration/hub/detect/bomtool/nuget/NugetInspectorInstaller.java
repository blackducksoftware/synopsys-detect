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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class NugetInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorInstaller.class);

    private final DirectoryManager directoryManager;
    private final AirGapManager airGapManager;
    private final DetectConfiguration detectConfiguration;
    private final ExecutableRunner executableRunner;

    private String resolvedNugetInspectorExecutable = null;

    public NugetInspectorInstaller(final DirectoryManager directoryManager, final AirGapManager airGapManager, final DetectConfiguration detectConfiguration,
        final ExecutableRunner executableRunner) {
        this.directoryManager = directoryManager;
        this.airGapManager = airGapManager;
        this.detectConfiguration = detectConfiguration;
        this.executableRunner = executableRunner;
    }

    public String install(final String nugetInspectorVersion, final String nugetExecutablePath) throws DetectUserFriendlyException, ExecutableRunnerException, IOException {
        if (resolvedNugetInspectorExecutable == null) {
            resolvedNugetInspectorExecutable = installInspector(nugetExecutablePath, directoryManager.getSharedDirectory("nuget"), nugetInspectorVersion);
            if (resolvedNugetInspectorExecutable == null) {
                throw new DetectUserFriendlyException("Unable to install nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        } else {
            throw new DetectUserFriendlyException("Unable to resolve nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
        }

        return resolvedNugetInspectorExecutable;
    }

    private String installInspector(final String nugetExecutablePath, final File outputDirectory, final String inspectorVersion) throws IOException, ExecutableRunnerException {
        final File toolsDirectory;
        final String nugetInspectorName = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None);

        final File airGapNugetInspectorDirectory = new File(airGapManager.getNugetInspectorAirGapPath());
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug("Running in airgap mode. Resolving from local path");
            toolsDirectory = new File(airGapNugetInspectorDirectory, "tools");
        } else {
            logger.debug("Running online. Resolving through nuget");

            for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL, PropertyAuthority.None)) {
                logger.debug("Attempting source: " + source);
                final boolean success = attemptInstallInspectorFromSource(source, nugetExecutablePath, outputDirectory, inspectorVersion);
                if (success) {
                    break;
                }
            }
            final String inspectorDirectoryName = nugetInspectorName + "." + inspectorVersion;
            final File inspectorVersionDirectory = new File(outputDirectory, inspectorDirectoryName);
            toolsDirectory = new File(inspectorVersionDirectory, "tools");
        }
        final String exeName = nugetInspectorName + ".exe";
        final File inspectorExe = new File(toolsDirectory, exeName);

        if (!inspectorExe.exists()) {
            logger.warn(String.format("Could not find the %s version: %s even after an install attempt.", nugetInspectorName, inspectorVersion));
            return null;
        }

        return inspectorExe.getCanonicalPath();
    }

    private boolean attemptInstallInspectorFromSource(final String source, final String nugetExecutablePath, final File outputDirectory, final String resolvedInspectorVersion) throws IOException, ExecutableRunnerException {
        final List<String> nugetOptions = new ArrayList<>(Arrays.asList(
            "install",
            detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None),
            "-OutputDirectory",
            outputDirectory.getCanonicalPath(),
            "-Source",
            source,
            "-Version",
            resolvedInspectorVersion)
        );

        final Optional<String> nugetConfigPath = detectConfiguration.getOptionalProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH, PropertyAuthority.None);
        if (nugetConfigPath.isPresent()) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfigPath.get());
        }

        final Executable installInspectorExecutable = new Executable(directoryManager.getSourceDirectory(), nugetExecutablePath, nugetOptions);
        final ExecutableOutput result = executableRunner.execute(installInspectorExecutable);

        return result.getReturnCode() == 0 && result.getErrorOutputAsList().size() == 0;
    }
}
