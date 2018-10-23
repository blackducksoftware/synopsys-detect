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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class NugetInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class);

    private final DirectoryManager directoryManager;
    private final ExecutableManager executableManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final AirGapManager airGapManager;

    private boolean hasResolvedInspector;
    private String resolvedNugetInspectorExecutable;
    private String resolvedInspectorVersion;

    public NugetInspectorManager(final DirectoryManager directoryManager, final ExecutableManager executableManager, final ExecutableRunner executableRunner,
        final DetectConfiguration detectConfiguration, final AirGapManager airGapManager) {
        this.directoryManager = directoryManager;
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.airGapManager = airGapManager;
    }

    public String findNugetInspector() throws BomToolException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                install();
            }

            return resolvedNugetInspectorExecutable;
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

    public void install() throws DetectUserFriendlyException, ExecutableRunnerException, IOException {
        final String nugetExecutable = executableManager
                                           .getExecutablePathOrOverride(ExecutableType.NUGET, true, directoryManager.getSourceDirectory(),
                                               detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_PATH, PropertyAuthority.None));

        if (nugetExecutable == null) {
            throw new DetectUserFriendlyException("Unable to find a nuget executable even though nuget applied.", ExitCodeType.FAILURE_CONFIGURATION);
        } else {
            resolvedInspectorVersion = resolveInspectorVersion(nugetExecutable);
            if (resolvedInspectorVersion != null) {
                resolvedNugetInspectorExecutable = installInspector(nugetExecutable, directoryManager.getSharedDirectory("nuget"), resolvedInspectorVersion);
                if (resolvedNugetInspectorExecutable == null) {
                    throw new DetectUserFriendlyException("Unable to install nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
                }
            } else {
                throw new DetectUserFriendlyException("Unable to resolve nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        }
    }

    private String resolveInspectorVersion(final String nugetExecutablePath) throws ExecutableRunnerException {
        final String nugetInspectorPackageVersion = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_VERSION, PropertyAuthority.None);
        if ("latest".equalsIgnoreCase(nugetInspectorPackageVersion)) {
            if (shouldUseAirGap()) {
                logger.debug("Running in airgap mode. Resolving version from local path");
                return resolveVersionFromSource(airGapManager.getNugetInspectorAirGapPath(), nugetExecutablePath);
            } else {
                logger.debug("Running online. Resolving version through nuget");
                for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL, PropertyAuthority.None)) {
                    logger.debug("Attempting source: " + source);
                    final String inspectorVersion = resolveVersionFromSource(source, nugetExecutablePath);
                    if (inspectorVersion != null) {
                        return inspectorVersion;
                    }
                }
            }
        } else {
            return nugetInspectorPackageVersion;
        }
        return null;
    }

    private boolean shouldUseAirGap() {
        final File airGapNugetInspectorDirectory = new File(airGapManager.getNugetInspectorAirGapPath());
        return airGapNugetInspectorDirectory.exists();
    }

    private String resolveVersionFromSource(final String source, final String nugetExecutablePath) throws ExecutableRunnerException {
        String version = null;

        final List<String> nugetOptions = new ArrayList<>();

        nugetOptions.addAll(Arrays.asList(
            "list",
            detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None),
            "-Source",
            source));

        final String nugetConfigPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH, PropertyAuthority.None);
        if (StringUtils.isNotBlank(nugetConfigPath)) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfigPath);
        }

        final Executable getInspectorVersionExecutable = new Executable(directoryManager.getSourceDirectory(), nugetExecutablePath, nugetOptions);

        final List<String> output = executableRunner.execute(getInspectorVersionExecutable).getStandardOutputAsList();
        for (final String line : output) {
            final String[] lineChunks = line.split(" ");
            if (detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None).equalsIgnoreCase(lineChunks[0])) {
                version = lineChunks[1];
            }
        }

        return version;

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
                final boolean success = attemptInstallInspectorFromSource(source, nugetExecutablePath, outputDirectory);
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

    private boolean attemptInstallInspectorFromSource(final String source, final String nugetExecutablePath, final File outputDirectory) throws IOException, ExecutableRunnerException {
        final List<String> nugetOptions = new ArrayList<>();

        nugetOptions.addAll(Arrays.asList(
            "install",
            detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None),
            "-OutputDirectory",
            outputDirectory.getCanonicalPath(),
            "-Source",
            source,
            "-Version",
            resolvedInspectorVersion));
        final String nugetConfigPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH, PropertyAuthority.None);
        if (StringUtils.isNotBlank(nugetConfigPath)) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfigPath);
        }

        final Executable installInspectorExecutable = new Executable(directoryManager.getSourceDirectory(), nugetExecutablePath, nugetOptions);
        final ExecutableOutput result = executableRunner.execute(installInspectorExecutable);

        if (result.getReturnCode() == 0 && result.getErrorOutputAsList().size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
