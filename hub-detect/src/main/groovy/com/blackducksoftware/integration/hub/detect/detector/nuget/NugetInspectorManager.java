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
package com.blackducksoftware.integration.hub.detect.detector.nuget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.nuget.inspector.DotNetCoreNugetInspector;
import com.blackducksoftware.integration.hub.detect.detector.nuget.inspector.ExeNugetInspector;
import com.blackducksoftware.integration.hub.detect.detector.nuget.inspector.NugetInspector;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
import com.blackducksoftware.integration.hub.detect.util.DetectZipUtil;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactResolver;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactoryConstants;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class NugetInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class);

    private final DirectoryManager directoryManager;
    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;
    private final AirGapManager airGapManager;
    private final ArtifactResolver artifactResolver;
    private final DetectInfo detectInfo;
    private final DetectFileFinder detectFileFinder;

    private boolean hasResolvedInspector;
    private NugetInspector resolvedNugetInspector;

    public NugetInspectorManager(final DirectoryManager directoryManager, final ExecutableFinder executableFinder,
        final DetectConfiguration detectConfiguration, final AirGapManager airGapManager, final ArtifactResolver artifactResolver, final DetectInfo detectInfo,
        final DetectFileFinder detectFileFinder) {
        this.directoryManager = directoryManager;
        this.executableFinder = executableFinder;
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

    public NugetInspector install() throws DetectUserFriendlyException, DetectorException, IOException {
        //dotnet
        final String dotnetExecutable = executableFinder
                                            .getExecutablePathOrOverride(ExecutableType.DOTNET, true, directoryManager.getSourceDirectory(),
                                                detectConfiguration.getProperty(DetectProperty.DETECT_DOTNET_PATH, PropertyAuthority.None));

        boolean useDotnet = true;
        if (shouldForceExeInspector(detectInfo) || dotnetExecutable == null) {
            logger.info("Will use the classic inspector.");
            useDotnet = false;
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
            File nugetDirectory = directoryManager.getSharedDirectory("nuget");
            //create the artifact
            String nugetInspectorVersion = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_VERSION, PropertyAuthority.None);
            Optional<String> source;
            if (useDotnet) {
                source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.NUGET_INSPECTOR_REPO, ArtifactoryConstants.NUGET_INSPECTOR_PROPERTY, nugetInspectorVersion,
                    ArtifactoryConstants.NUGET_INSPECTOR_VERSION_OVERRIDE);
            } else {
                source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_REPO, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_PROPERTY, nugetInspectorVersion,
                    ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_VERSION_OVERRIDE);
            }
            if (source.isPresent()) {
                String nupkgName = artifactResolver.parseFileName(source.get());
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
                    logger.info("Found nuget inspector folder.");
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
            return new DotNetCoreNugetInspector(dotnetExecutable, inspectorExe);
        } else {
            throw new DetectorException("Unable to find nuget inspector, looking for " + dotnetInspectorName + " in " + toolsFolder.toString());
        }
    }

    private ExeNugetInspector findExeInspector(File nupkgFolder) throws DetectorException {
        //original inspector
        final String exeInspectorName = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME, PropertyAuthority.None);
        logger.info("Searching for: " + exeInspectorName);
        File toolsFolder = new File(nupkgFolder, "tools");
        Optional<File> foundExe = detectFileFinder.findFilesToDepth(toolsFolder, exeInspectorName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new ExeNugetInspector(inspectorExe);
        } else {
            throw new DetectorException("Unable to find nuget inspector, looking for " + exeInspectorName + " in " + toolsFolder.toString());
        }
    }

    private boolean shouldForceExeInspector(DetectInfo detectInfo) {

        if (detectInfo.getCurrentOs() != OperatingSystemType.WINDOWS) {
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
}
