/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.projectinspector;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.OperatingSystemType;

public class ArtifactoryProjectInspectorInstaller {
    private final DetectInfo detectInfo;
    private final ArtifactoryZipInstaller artifactoryZipInstaller;
    private final ProjectInspectorExecutableLocator projectInspectorExecutableLocator;

    public ArtifactoryProjectInspectorInstaller(DetectInfo detectInfo,
        ArtifactoryZipInstaller artifactoryZipInstaller, ProjectInspectorExecutableLocator projectInspectorExecutableLocator) {
        this.detectInfo = detectInfo;
        this.artifactoryZipInstaller = artifactoryZipInstaller;
        this.projectInspectorExecutableLocator = projectInspectorExecutableLocator;
    }

    @Nullable
    public File install(File directory) throws DetectableException {
        if (detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS) {
            return install(directory, ArtifactoryConstants.PROJECT_INSPECTOR_WINDOWS_PROPERTY);
        } else if (detectInfo.getCurrentOs() == OperatingSystemType.MAC) {
            return install(directory, ArtifactoryConstants.PROJECT_INSPECTOR_MAC_PROPERTY);
        } else {
            return install(directory, ArtifactoryConstants.PROJECT_INSPECTOR_LINUX_PROPERTY);
        }
    }

    @Nullable
    public File install(File installDirectory, String property) throws DetectableException {
        File extractedZip = downloadZip(property, installDirectory);
        return projectInspectorExecutableLocator.findExecutable(extractedZip);
    }

    @NotNull //Returns location of extracted zip or throws
    public File downloadZip(String property, File installDirectory) throws DetectableException {
        try {
            File zip = artifactoryZipInstaller.installZipFromSource(installDirectory, ".zip", ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.PROJECT_INSPECTOR_REPO, property);
            return zip;
        } catch (IntegrationException | IOException e) {
            throw new DetectableException("Unable to install the project inspector from Artifactory.", e);
        }
    }

}
