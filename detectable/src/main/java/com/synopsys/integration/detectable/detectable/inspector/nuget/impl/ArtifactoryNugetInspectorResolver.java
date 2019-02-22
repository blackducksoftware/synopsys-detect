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
package com.synopsys.integration.detectable.detectable.inspector.nuget.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.exception.IntegrationException;

public class ArtifactoryNugetInspectorResolver implements NugetInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(ArtifactoryNugetInspectorResolver.class);

    @Override
    public NugetInspector resolveNugetInspector() {
        return null; //TODO: RETURN IT
    }

    /*
    private final DirectoryManager directoryManager;
    private final ExecutableFinder executableFinder;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final AirGapManager airGapManager;
    private final ArtifactResolver artifactResolver;
    private final DetectInfo detectInfo;
    private final DetectFileFinder detectFileFinder;

    private boolean hasResolvedInspector;
    private NugetInspector resolvedNugetInspector;

    public ArtifactoryNugetInspectorResolver(final DirectoryManager directoryManager, final ExecutableFinder executableFinder,
        final ExecutableRunner executableRunner, final DetectConfiguration detectConfiguration, final AirGapManager airGapManager, final ArtifactResolver artifactResolver,
        final DetectInfo detectInfo,
        final DetectFileFinder detectFileFinder) {
        this.directoryManager = directoryManager;
        this.executableFinder = executableFinder;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.airGapManager = airGapManager;
        this.artifactResolver = artifactResolver;
        this.detectInfo = detectInfo;
        this.detectFileFinder = detectFileFinder;
    }

    public NugetInspector findNugetInspector() throws DetectorException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedNugetInspector = install();
            }

            return resolvedNugetInspector;
        } catch (final Exception e) {
            throw new DetectorException(e);
        }
    }

    public NugetInspector install() throws DetectUserFriendlyException, IntegrationException, IOException {
        //dotnet
        final String dotnetExecutable = executableFinder
                                            .getExecutablePathOrOverride(ExecutableType.DOTNET, true, directoryManager.getSourceDirectory(),
                                                detectConfiguration.getProperty(DetectProperty.DETECT_DOTNET_PATH, PropertyAuthority.None));

        boolean useDotnet = true;
        if (shouldForceExeInspector(detectInfo)) {
            logger.info("Will use the classic inspector.");
            useDotnet = false;
        } else if (dotnetExecutable == null) {
            if (isNotWindows(detectInfo)) {
                throw new DetectorException("When not on Windows, the nuget inspector requires the dotnet executable to run.");
            } else {
                useDotnet = false;
            }
        }

        if (shouldUseAirGap()) {
            logger.debug("Running in airgap mode. Resolving from local path");
            File nugetFolder = new File(airGapManager.getNugetInspectorAirGapPath());
            if (useDotnet) {
                File dotnetFolder = new File(nugetFolder, "nuget_dotnet");
                return findDotnetCoreInspector(dotnetFolder, dotnetExecutable);
            } else {
                File classicFolder = new File(nugetFolder, "nuget_classic");
                return findExeInspector(classicFolder);
            }

        } else {
            logger.info("Determining the nuget inspector version.");
            File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            //create the artifact
            String nugetInspectorVersion = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_VERSION, PropertyAuthority.None);
            Optional<String> source;
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
                String nupkgName = artifactResolver.parseFileName(source.get());
                logger.debug("Parsed artifact name: " + nupkgName);
                File nupkgFile = new File(nugetDirectory, nupkgName);
                String inspectorFolderName = nupkgName.replace(".nupkg", "");
                File inspectorFolder = new File(nugetDirectory, inspectorFolderName);
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
                    throw new DetectorException("Unable to find inspector folder even after zip extraction attempt.");
                }
            } else {
                throw new DetectorException("Unable to find nuget inspector location in Artifactory.");
            }
        }
    }

    private DotNetCoreNugetInspector findDotnetCoreInspector(File nupkgFolder, String dotnetExecutable) throws DetectorException {
        //new inspector
        final String dotnetInspectorName = "BlackduckNugetInspector.dll";
        logger.info("Searching for: " + dotnetInspectorName);
        File toolsFolder = new File(nupkgFolder, "tools");
        Optional<File> foundExe = detectFileFinder.findFilesToDepth(toolsFolder, dotnetInspectorName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new DotNetCoreNugetInspector(dotnetExecutable, inspectorExe, executableRunner);
        } else {
            throw new DetectorException("Unable to find nuget inspector, looking for " + dotnetInspectorName + " in " + toolsFolder.toString());
        }
    }

    private ExeNugetInspector findExeInspector(File nupkgFolder) throws DetectorException {
        //original inspector
        final String exeInspectorName = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None);
        final String exeName = exeInspectorName + ".exe";
        logger.info("Searching for: " + exeName);
        File toolsFolder = new File(nupkgFolder, "tools");
        logger.debug("Searching in: " + toolsFolder.getAbsolutePath());
        Optional<File> foundExe = detectFileFinder.findFilesToDepth(toolsFolder, exeName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new ExeNugetInspector(executableRunner, inspectorExe);
        } else {
            throw new DetectorException("Unable to find nuget inspector named '" + exeName + "' in " + toolsFolder.getAbsolutePath());
        }
    }

    private boolean isWindows(DetectInfo detectInfo) {
        if (detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS) {
            return true;
        }
        return false;
    }

    private boolean isNotWindows(DetectInfo detectInfo) {
        return !isWindows(detectInfo);
    }

    private boolean shouldForceExeInspector(DetectInfo detectInfo) {

        if (isNotWindows(detectInfo)) {
            return false;
        }

        //if customers have overridden the repo url's and include a v2 api, we must use the old nuget inspector (exe inspector) until 5.0.0 of detect.
        //TODO: Remove in 6.0.0
        for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL, PropertyAuthority.None)) {
            if (source.contains("v2")) {
                logger.warn("You are using Version 2 of the Nuget Api. Please update to version 3. Support for 2 is deprecated.");
                return true;
            }
        }
        return false;
    }

    private boolean shouldUseAirGap() {
        final File airGapNugetInspectorDirectory = new File(airGapManager.getNugetInspectorAirGapPath());
        return airGapNugetInspectorDirectory.exists();
    }

    */
}
