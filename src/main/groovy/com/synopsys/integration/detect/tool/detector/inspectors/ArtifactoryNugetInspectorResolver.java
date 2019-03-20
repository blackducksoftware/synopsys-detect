/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableResolver;
import com.synopsys.integration.detect.type.OperatingSystemType;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.file.AirGapManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.DotNetCoreNugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.ExeNugetInspector;
import com.synopsys.integration.exception.IntegrationException;

public class ArtifactoryNugetInspectorResolver implements NugetInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(ArtifactoryNugetInspectorResolver.class);

    private final DirectoryManager directoryManager;
    private final DetectExecutableResolver executableResolver;
    private final ExecutableRunner executableRunner;
    private final AirGapManager airGapManager;
    private final ArtifactResolver artifactResolver;
    private final DetectInfo detectInfo;
    private final FileFinder fileFinder;
    private final NugetInspectorOptions nugetInspectorOptions;

    private boolean hasResolvedInspector;
    private NugetInspector resolvedNugetInspector;

    public ArtifactoryNugetInspectorResolver(final DirectoryManager directoryManager, final DetectExecutableResolver executableResolver, final ExecutableRunner executableRunner, final AirGapManager airGapManager,
        final ArtifactResolver artifactResolver, final DetectInfo detectInfo, final FileFinder fileFinder, final NugetInspectorOptions nugetInspectorOptions) {
        this.directoryManager = directoryManager;
        this.executableResolver = executableResolver;
        this.executableRunner = executableRunner;
        this.airGapManager = airGapManager;
        this.artifactResolver = artifactResolver;
        this.detectInfo = detectInfo;
        this.fileFinder = fileFinder;
        this.nugetInspectorOptions = nugetInspectorOptions;
    }

    @Override
    public NugetInspector resolveNugetInspector() throws DetectableException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedNugetInspector = install();
            }

            return resolvedNugetInspector;
        } catch (final Exception e) {
            throw new DetectableException(e);
        }
    }

    private NugetInspector install() throws DetectUserFriendlyException, IntegrationException, IOException {
        //dotnet
        final File dotnetExecutable = executableResolver.resolveDotNet();

        boolean useDotnet = true;
        if (shouldForceExeInspector(detectInfo)) {
            logger.info("Will use the classic inspector.");
            useDotnet = false;
        } else if (dotnetExecutable == null) {
            if (isNotWindows(detectInfo)) {
                throw new DetectableException("When not on Windows, the nuget inspector requires the dotnet executable to run.");
            } else {
                useDotnet = false;
            }
        }

        Optional<File> nugetAirGapPath = airGapManager.getNugetInspectorAirGapFile();
        if (nugetAirGapPath.isPresent()) {
            logger.debug("Running in airgap mode. Resolving from local path");
            if (useDotnet) {
                final File dotnetFolder = new File(nugetAirGapPath.get(), "nuget_dotnet");
                return findDotnetCoreInspector(dotnetFolder, dotnetExecutable);
            } else {
                final File classicFolder = new File(nugetAirGapPath.get(), "nuget_classic");
                return findExeInspector(classicFolder);
            }
        } else {
            logger.info("Determining the nuget inspector version.");
            final File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            //create the artifact
            final String nugetInspectorVersion = nugetInspectorOptions.getNugetInspectorVersion();
            final Optional<String> source;
            if (useDotnet) {
                logger.info("Will attempt to resolve the dotnet inspector version.");
                source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.NUGET_INSPECTOR_REPO, ArtifactoryConstants.NUGET_INSPECTOR_PROPERTY, nugetInspectorVersion,
                    ArtifactoryConstants.NUGET_INSPECTOR_VERSION_OVERRIDE);

            } else {
                logger.info("Will attempt to resolve the classic inspector version.");
                source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_REPO, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_PROPERTY, nugetInspectorVersion,
                    ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_VERSION_OVERRIDE);
            }
            if (source.isPresent()) {
                logger.debug("Resolved the nuget inspector url: " + source.get());
                final String nupkgName = artifactResolver.parseFileName(source.get());
                logger.debug("Parsed artifact name: " + nupkgName);
                final File nupkgFile = new File(nugetDirectory, nupkgName);
                final String inspectorFolderName = nupkgName.replace(".nupkg", "");
                final File inspectorFolder = new File(nugetDirectory, inspectorFolderName);
                if (!inspectorFolder.exists()) {
                    logger.info("Downloading nuget inspector.");
                    artifactResolver.downloadArtifact(nupkgFile, source.get());
                    logger.info("Extracting nuget inspector.");
                    DetectZipUtil.unzip(nupkgFile, inspectorFolder, Charset.defaultCharset());
                }
                if (inspectorFolder.exists()) {
                    logger.info("Found nuget inspector folder. Looking for inspector.");
                    if (useDotnet) {
                        return findDotnetCoreInspector(inspectorFolder, dotnetExecutable);
                    } else {
                        return findExeInspector(inspectorFolder);
                    }
                } else {
                    throw new DetectableException("Unable to find inspector folder even after zip extraction attempt.");
                }
            } else {
                throw new DetectableException("Unable to find nuget inspector location in Artifactory.");
            }
        }
    }

    private DotNetCoreNugetInspector findDotnetCoreInspector(final File nupkgFolder, final File dotnetExecutable) throws DetectableException {
        //new inspector
        final String dotnetInspectorName = "BlackduckNugetInspector.dll";
        logger.info("Searching for: " + dotnetInspectorName);
        final File toolsFolder = new File(nupkgFolder, "tools");
        final Optional<File> foundExe = fileFinder.findFiles(toolsFolder, dotnetInspectorName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            final String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new DotNetCoreNugetInspector(dotnetExecutable, inspectorExe, executableRunner);
        } else {
            throw new DetectableException("Unable to find nuget inspector, looking for " + dotnetInspectorName + " in " + toolsFolder.toString());
        }
    }

    private ExeNugetInspector findExeInspector(final File nupkgFolder) throws DetectableException {
        //original inspector
        final String exeName = nugetInspectorOptions.getNugetInspectorName() + ".exe";
        logger.info("Searching for: " + exeName);
        final File toolsFolder = new File(nupkgFolder, "tools");
        logger.debug("Searching in: " + toolsFolder.getAbsolutePath());
        final Optional<File> foundExe = fileFinder.findFiles(toolsFolder, exeName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            final String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new ExeNugetInspector(executableRunner, inspectorExe);
        } else {
            throw new DetectableException("Unable to find nuget inspector named '" + exeName + "' in " + toolsFolder.getAbsolutePath());
        }
    }

    private boolean isWindows(final DetectInfo detectInfo) {
        return detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS;
    }

    private boolean isNotWindows(final DetectInfo detectInfo) {
        return !isWindows(detectInfo);
    }

    private boolean shouldForceExeInspector(final DetectInfo detectInfo) {
        if (isNotWindows(detectInfo)) {
            return false;
        }

        //if customers have overridden the repo url's and include a v2 api, we must use the old nuget inspector (exe inspector) until 5.0.0 of detect.
        //TODO: Remove in 6.0.0
        for (final String source : nugetInspectorOptions.getPackagesRepoUrl()) {
            if (source.contains("v2")) {
                logger.warn("You are using Version 2 of the Nuget Api. Please update to version 3. Support for 2 is deprecated.");
                return true;
            }
        }
        return false;
    }
}
