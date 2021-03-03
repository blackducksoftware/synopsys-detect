/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public File installDotNet3(final File destination, @Nullable final String overrideVersion) throws DetectableException {
        logger.debug("Will attempt to resolve the dotnet3 inspector version.");
        return installInspector(destination, overrideVersion, ArtifactoryConstants.NUGET_DOTNET3_INSPECTOR_REPO, ArtifactoryConstants.NUGET_DOTNET3_INSPECTOR_PROPERTY, ArtifactoryConstants.NUGET_DOTNET3_INSPECTOR_VERSION_OVERRIDE);
    }

    public File installDotNet(final File destination, @Nullable final String overrideVersion) throws DetectableException {
        logger.debug("Will attempt to resolve the dotnet inspector version.");
        return installInspector(destination, overrideVersion, ArtifactoryConstants.NUGET_INSPECTOR_REPO, ArtifactoryConstants.NUGET_INSPECTOR_PROPERTY, ArtifactoryConstants.NUGET_INSPECTOR_VERSION_OVERRIDE);
    }

    public File installExeInspector(final File destination, @Nullable final String overrideVersion) throws DetectableException {
        logger.debug("Will attempt to resolve the classic inspector version.");
        return installInspector(destination, overrideVersion, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_REPO, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_PROPERTY, ArtifactoryConstants.CLASSIC_NUGET_INSPECTOR_VERSION_OVERRIDE);
    }

    private File installInspector(final File destination, @Nullable final String overrideVersion, final String inspectorRepo, final String inspectorProperty, final String inspectorVersionOverride) throws DetectableException {
        try {
            final String source = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, inspectorRepo, inspectorProperty, StringUtils.defaultString(overrideVersion), inspectorVersionOverride);
            return installFromSource(destination, source);
        } catch (final Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }

    private File installFromSource(final File dest, final String source) throws IntegrationException, IOException {
        logger.debug("Resolved the nuget inspector url: " + source);
        final String nupkgName = artifactResolver.parseFileName(source);
        logger.debug("Parsed artifact name: " + nupkgName);
        final String inspectorFolderName = nupkgName.replace(".nupkg", "");
        final File inspectorFolder = new File(dest, inspectorFolderName);
        if (!inspectorFolder.exists()) {
            logger.debug("Downloading nuget inspector.");
            final File nupkgFile = new File(dest, nupkgName);
            artifactResolver.downloadArtifact(nupkgFile, source);
            logger.debug("Extracting nuget inspector.");
            DetectZipUtil.unzip(nupkgFile, inspectorFolder, Charset.defaultCharset());
            FileUtils.deleteQuietly(nupkgFile);
        } else {
            logger.debug("Inspector is already downloaded, folder exists.");
        }
        return inspectorFolder;
    }
}
