package com.synopsys.integration.detect.tool.detector.inspector.nuget;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;

public class OnlineNugetInspectorResolver implements NugetInspectorResolver {
    private static final String INSPECTOR_NAME = "detect-nuget-inspector";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtifactoryNugetInspectorInstaller installer;
    private final DirectoryManager directoryManager;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    private boolean hasResolvedInspector = false;
    private ExecutableTarget inspector = null;

    public OnlineNugetInspectorResolver(
        ArtifactoryNugetInspectorInstaller installer,
        DirectoryManager directoryManager,
        InstalledToolManager installedToolManager,
        InstalledToolLocator installedToolLocator
    ) {
        this.installer = installer;
        this.directoryManager = directoryManager;
        this.installedToolManager = installedToolManager;
        this.installedToolLocator = installedToolLocator;
    }

    @Override
    public ExecutableTarget resolveNugetInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            File inspectorFile = null;
            hasResolvedInspector = true;

            File installDirectory = directoryManager.getPermanentDirectory(INSPECTOR_NAME);
            try {
                inspectorFile = installer.install(installDirectory);
            } catch (DetectableException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Unable to install the detect nuget inspector from Artifactory.", e);
                } else {
                    logger.warn("Unable to install the detect nuget inspector from Artifactory.");
                }
            }

            if (inspectorFile == null) {
                // Remote installation has failed
                logger.debug("Attempting to locate previous install of detect nuget inspector.");
                inspectorFile = installedToolLocator.locateTool(INSPECTOR_NAME)
                    .orElseThrow(() -> {
                        logger.warn("Unable to locate previous install of the detect nuget inspector.");
                        return new DetectableException("Unable to locate previous install of the detect nuget inspector.");
                    });
            } else {
                installedToolManager.saveInstalledToolLocation(INSPECTOR_NAME, inspectorFile.getAbsolutePath());
            }

            inspector = ExecutableTarget.forFile(inspectorFile);
        }
        return inspector;
    }
}
