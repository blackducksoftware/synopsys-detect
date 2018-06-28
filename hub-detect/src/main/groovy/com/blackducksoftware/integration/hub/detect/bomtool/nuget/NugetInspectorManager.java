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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class NugetInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class);

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private boolean hasResolvedInspector;
    private String resolvedNugetInspectorExecutable;
    private String resolvedInspectorVersion;

    public String findNugetInspector(final BomToolEnvironment environment) throws BomToolException {
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
        final String nugetExecutable = executableManager.getExecutablePathOrOverride(ExecutableType.NUGET, true, detectConfiguration.getSourceDirectory(), detectConfiguration.getNugetPath());
        resolvedInspectorVersion = resolveInspectorVersion(nugetExecutable);
        if (resolvedInspectorVersion != null) {
            resolvedNugetInspectorExecutable = installInspector(nugetExecutable, detectFileManager.getSharedDirectory("nuget"), resolvedInspectorVersion);
            if (resolvedNugetInspectorExecutable == null) {
                throw new DetectUserFriendlyException("Unable to install nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        } else {
            throw new DetectUserFriendlyException("Unable to resolve nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private String resolveInspectorVersion(final String nugetExecutablePath) throws ExecutableRunnerException {
        if ("latest".equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
            if (shouldUseAirGap()) {
                logger.debug("Running in airgap mode. Resolving version from local path");
                return resolveVersionFromSource(detectConfiguration.getNugetInspectorAirGapPath(), nugetExecutablePath);
            } else {
                logger.debug("Running online. Resolving version through nuget");
                for (final String source : detectConfiguration.getNugetPackagesRepoUrl()) {
                    logger.debug("Attempting source: " + source);
                    final String inspectorVersion = resolveVersionFromSource(source, nugetExecutablePath);
                    if (inspectorVersion != null) {
                        return inspectorVersion;
                    }
                }
            }
        } else {
            return detectConfiguration.getNugetInspectorPackageVersion();
        }
        return null;
    }

    private boolean shouldUseAirGap() {
        final File airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath());
        return airGapNugetInspectorDirectory.exists();
    }

    private String resolveVersionFromSource(final String source, final String nugetExecutablePath) throws ExecutableRunnerException {
        String version = null;

        final List<String> nugetOptions = new ArrayList<>();

        nugetOptions.addAll(Arrays.asList(
                "list",
                detectConfiguration.getNugetInspectorPackageName(),
                "-Source",
                source));

        if (StringUtils.isNotBlank(detectConfiguration.getNugetConfigPath())) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(detectConfiguration.getNugetConfigPath());
        }

        final Executable getInspectorVersionExecutable = new Executable(detectConfiguration.getSourceDirectory(), nugetExecutablePath, nugetOptions);

        final List<String> output = executableRunner.execute(getInspectorVersionExecutable).getStandardOutputAsList();
        for (final String line : output) {
            final String[] lineChunks = line.split(" ");
            if (detectConfiguration.getNugetInspectorPackageName().equalsIgnoreCase(lineChunks[0])) {
                version = lineChunks[1];
            }
        }

        return version;

    }

    private String installInspector(final String nugetExecutablePath, final File outputDirectory, final String inspectorVersion) throws IOException, ExecutableRunnerException {
        final File toolsDirectory;

        final File airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath());
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug("Running in airgap mode. Resolving from local path");
            toolsDirectory = new File(airGapNugetInspectorDirectory, "tools");
        } else {
            logger.debug("Running online. Resolving through nuget");

            for (final String source : detectConfiguration.getNugetPackagesRepoUrl()) {
                logger.debug("Attempting source: " + source);
                final boolean success = attemptInstallInspectorFromSource(source, nugetExecutablePath, outputDirectory);
                if (success) {
                    break;
                }
            }
            final String inspectorDirectoryName = detectConfiguration.getNugetInspectorPackageName() + "." + inspectorVersion;
            final File inspectorVersionDirectory = new File(outputDirectory, inspectorDirectoryName);
            toolsDirectory = new File(inspectorVersionDirectory, "tools");
        }
        final String exeName = detectConfiguration.getNugetInspectorPackageName() + ".exe";
        final File inspectorExe = new File(toolsDirectory, exeName);

        if (!inspectorExe.exists()) {
            logger.warn(String.format("Could not find the %s version: %s even after an install attempt.", detectConfiguration.getNugetInspectorPackageName(), inspectorVersion));
            return null;
        }

        return inspectorExe.getCanonicalPath();
    }

    private boolean attemptInstallInspectorFromSource(final String source, final String nugetExecutablePath, final File outputDirectory) throws IOException, ExecutableRunnerException {
        final List<String> nugetOptions = new ArrayList<>();

        nugetOptions.addAll(Arrays.asList(
                "install",
                detectConfiguration.getNugetInspectorPackageName(),
                "-OutputDirectory",
                outputDirectory.getCanonicalPath(),
                "-Source",
                source,
                "-Version",
                resolvedInspectorVersion));

        if (StringUtils.isNotBlank(detectConfiguration.getNugetConfigPath())) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(detectConfiguration.getNugetConfigPath());
        }

        final Executable installInspectorExecutable = new Executable(detectConfiguration.getSourceDirectory(), nugetExecutablePath, nugetOptions);
        final ExecutableOutput result = executableRunner.execute(installInspectorExecutable);

        if (result.getReturnCode() == 0 && result.getErrorOutputAsList().size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
