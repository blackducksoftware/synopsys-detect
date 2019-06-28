/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.exception.IntegrationException;

public class NugetInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArtifactResolver artifactResolver;

    public NugetInspectorInstaller(final ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    public File installDotNet(File destination, Optional<String> overrideVersion) throws DetectableException {
        try {
            logger.info("Will attempt to resolve the dotnet inspector version.");
            Optional<String> source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.NUGET_INSPECTOR_REPO, ArtifactoryConstants.NUGET_INSPECTOR_PROPERTY,
                overrideVersion.orElse(""),
                ArtifactoryConstants.NUGET_INSPECTOR_VERSION_OVERRIDE);
            return installFromSource(destination, source);
        } catch (Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }

    public File installExeInspector(File destination, Optional<String> overrideVersion) throws DetectableException {
        try {
            logger.info("Will attempt to resolve the classic inspector version.");
            Optional<String> source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_REPO, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_PROPERTY,
                overrideVersion.orElse(""),
                ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_VERSION_OVERRIDE);
            return installFromSource(destination, source);
        } catch (Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }

    private File installFromSource(File dest, Optional<String> source) throws IntegrationException, IOException, DetectUserFriendlyException {
        if (source.isPresent()) {
            logger.debug("Resolved the nuget inspector url: " + source.get());
            final String nupkgName = artifactResolver.parseFileName(source.get());
            logger.debug("Parsed artifact name: " + nupkgName);
            final File nupkgFile = new File(dest, nupkgName);
            final String inspectorFolderName = nupkgName.replace(".nupkg", "");
            File inspectorFolder = new File(dest, inspectorFolderName);
            if (!inspectorFolder.exists()) {
                logger.info("Downloading nuget inspector.");
                artifactResolver.downloadArtifact(nupkgFile, source.get());
                logger.info("Extracting nuget inspector.");
                DetectZipUtil.unzip(nupkgFile, inspectorFolder, Charset.defaultCharset());
                return inspectorFolder;
            } else {
                logger.debug("Inspector is already downloaded, folder exists.");
                return inspectorFolder;
            }
        } else {
            throw new DetectableException("Unable to find nuget inspector location in Artifactory.");
        }
    }
}
