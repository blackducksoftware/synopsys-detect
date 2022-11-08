package com.synopsys.integration.detect.tool.detector.inspector.projectinspector;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer.ProjectInspectorInstaller;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class OnlineProjectInspectorResolver implements com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver {
    private static final String INSTALLED_TOOL_JSON_KEY = "project-inspector";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectInspectorInstaller projectInspectorInstaller;
    private final DirectoryManager directoryManager;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    private File projectInspectorExeFile = null;
    private boolean hasResolvedProjectInspectorExe = false;

    public OnlineProjectInspectorResolver(
        ProjectInspectorInstaller projectInspectorInstaller,
        DirectoryManager directoryManager,
        InstalledToolManager installedToolManager,
        InstalledToolLocator installedToolLocator
    ) {
        this.projectInspectorInstaller = projectInspectorInstaller;
        this.directoryManager = directoryManager;
        this.installedToolManager = installedToolManager;
        this.installedToolLocator = installedToolLocator;
    }

    @Override
    public ExecutableTarget resolveProjectInspector() throws DetectableException {
        if (!hasResolvedProjectInspectorExe) {
            hasResolvedProjectInspectorExe = true;
            File installDirectory = directoryManager.getPermanentDirectory(INSTALLED_TOOL_JSON_KEY);
            try {
                projectInspectorExeFile = projectInspectorInstaller.install(installDirectory);
            } catch (DetectableException e) {
                logger.warn("Unable to install the project inspector.");
            }
            if (projectInspectorExeFile == null) {
                if (projectInspectorInstaller.shouldFallbackToPreviousInstall()) {
                    return findExistingInstallation();
                } else {
                    logger.warn("Unable to locate given project inspector zip file.");
                    throw new DetectableException("Unable to locate given project inspector zip file.");
                }
            } else {
                installedToolManager.saveInstalledToolLocation(INSTALLED_TOOL_JSON_KEY, projectInspectorExeFile.getAbsolutePath());
            }
        }
        return ExecutableTarget.forFile(projectInspectorExeFile);
    }

    private ExecutableTarget findExistingInstallation() throws DetectableException {
        logger.debug("Attempting to locate previous install of project inspector.");
        return installedToolLocator.locateTool(INSTALLED_TOOL_JSON_KEY)
            .map(ExecutableTarget::forFile)
            .orElseThrow(() -> {
                logger.warn("Unable to locate previous install of the project inspector.");
                return new DetectableException("Unable to locate previous install of the project inspector.");
            });
    }
}
