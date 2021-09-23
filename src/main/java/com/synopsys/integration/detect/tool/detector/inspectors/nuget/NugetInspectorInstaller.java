/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class NugetInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArtifactoryZipInstaller artifactoryZipInstaller;

    public NugetInspectorInstaller(final ArtifactoryZipInstaller artifactoryZipInstaller) {
        this.artifactoryZipInstaller = artifactoryZipInstaller;
    }

    public File installDotNet5(final File destination, @Nullable final String overrideVersion) throws DetectableException {
        logger.debug("Will attempt to resolve the dotnet5 inspector version.");
        return installInspector(destination, overrideVersion, ArtifactoryConstants.NUGET_DOTNET5_INSPECTOR_REPO, ArtifactoryConstants.NUGET_DOTNET5_INSPECTOR_PROPERTY, ArtifactoryConstants.NUGET_DOTNET5_INSPECTOR_VERSION_OVERRIDE);
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
            return artifactoryZipInstaller.installZipFromSource(destination, ".nupkg", ArtifactoryConstants.ARTIFACTORY_URL, inspectorRepo, inspectorProperty, StringUtils.defaultString(overrideVersion), inspectorVersionOverride);
        } catch (final Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }
}
