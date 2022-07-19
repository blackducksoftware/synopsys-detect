package com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.OperatingSystemType;

public class ArtifactoryProjectInspectorInstaller implements ProjectInspectorInstaller {
    private final DetectInfo detectInfo;
    private final ArtifactoryZipInstaller artifactoryZipInstaller;
    private final ProjectInspectorExecutableLocator projectInspectorExecutableLocator;

    public ArtifactoryProjectInspectorInstaller(
        DetectInfo detectInfo,
        ArtifactoryZipInstaller artifactoryZipInstaller, ProjectInspectorExecutableLocator projectInspectorExecutableLocator
    ) {
        this.detectInfo = detectInfo;
        this.artifactoryZipInstaller = artifactoryZipInstaller;
        this.projectInspectorExecutableLocator = projectInspectorExecutableLocator;
    }

    @Override
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

    @Override
    public boolean shouldFallbackToPreviousInstall() {
        return true;
    }

    @NotNull //Returns location of extracted zip or throws
    public File downloadZip(String property, File installDirectory) throws DetectableException {
        try {
            return artifactoryZipInstaller.installZipFromSource(
                installDirectory,
                ".zip",
                ArtifactoryConstants.ARTIFACTORY_URL,
                ArtifactoryConstants.PROJECT_INSPECTOR_PROPERTY_REPO,
                property
            );
        } catch (IntegrationException | IOException e) {
            throw new DetectableException("Unable to install the project inspector from Artifactory.", e);
        }
    }

    @Nullable
    private File install(File installDirectory, String property) throws DetectableException {
        File extractedZip = downloadZip(property, installDirectory);
        return projectInspectorExecutableLocator.findExecutable(extractedZip);
    }
}
