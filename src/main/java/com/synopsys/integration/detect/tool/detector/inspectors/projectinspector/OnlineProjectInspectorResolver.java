package com.synopsys.integration.detect.tool.detector.inspectors.projectinspector;

import java.io.File;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class OnlineProjectInspectorResolver implements com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver {
    private static final String INSTALLED_TOOL_JSON_KEY = "project-inspector";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtifactoryProjectInspectorInstaller artifactoryProjectInspectorInstaller;
    private final LocalProjectInspectorInstaller localProjectInspectorInstaller;
    private final DirectoryManager directoryManager;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;
    @Nullable private final Path localProjectInspectorPath;

    File projectInspectorExeFile = null;
    private boolean hasResolvedProjectInspectorExe = false;

    public OnlineProjectInspectorResolver(
        ArtifactoryProjectInspectorInstaller artifactoryProjectInspectorInstaller,
        LocalProjectInspectorInstaller localProjectInspectorInstaller,
        DirectoryManager directoryManager,
        InstalledToolManager installedToolManager,
        InstalledToolLocator installedToolLocator,
        @Nullable Path localProjectInspectorPath
    ) {
        this.artifactoryProjectInspectorInstaller = artifactoryProjectInspectorInstaller;
        this.localProjectInspectorInstaller = localProjectInspectorInstaller;
        this.directoryManager = directoryManager;
        this.installedToolManager = installedToolManager;
        this.installedToolLocator = installedToolLocator;
        this.localProjectInspectorPath = localProjectInspectorPath;
    }

    @Override
    public ExecutableTarget resolveProjectInspector() throws DetectableException {
        if (!hasResolvedProjectInspectorExe) {
            hasResolvedProjectInspectorExe = true;
            File installDirectory = directoryManager.getPermanentDirectory(INSTALLED_TOOL_JSON_KEY);
            try {
                if (localProjectInspectorPath != null) {
                    File localInspectorZipFile = findFile(localProjectInspectorPath);
                    projectInspectorExeFile = localProjectInspectorInstaller.install(installDirectory, localInspectorZipFile);
                } else {
                    projectInspectorExeFile = artifactoryProjectInspectorInstaller.install(installDirectory);
                }
            } catch (DetectableException e) {
                logger.debug("Unable to install the project inspector from Artifactory.");
            }

            if (projectInspectorExeFile == null) {
                // Remote installation has failed
                logger.debug("Attempting to locate previous install of project inspector.");
                return installedToolLocator.locateTool(INSTALLED_TOOL_JSON_KEY)
                    .map(ExecutableTarget::forFile)
                    .orElseThrow(() ->
                        new DetectableException("Unable to locate previous install of the project inspector.")
                    );
            } else {
                installedToolManager.saveInstalledToolLocation(INSTALLED_TOOL_JSON_KEY, projectInspectorExeFile.getAbsolutePath());
            }
        }
        return ExecutableTarget.forFile(projectInspectorExeFile);
    }

    private File findFile(Path localProjectInspectorPath) throws DetectableException {
        logger.debug("Using user-provided project inspector zip path: {}", localProjectInspectorPath.toString());
        File providedZipCandidate = localProjectInspectorPath.toFile();
        if (providedZipCandidate.isFile()) {
            logger.debug("Found user-specified project inspector zip: {}", providedZipCandidate.getAbsolutePath());
            return providedZipCandidate;
        } else {
            String msg = String.format("Provided Project Inspector zip path (%s) does not exist or is not a file", providedZipCandidate.getAbsolutePath());
            logger.debug(msg);
            throw new DetectableException(msg);
        }
    }
}
