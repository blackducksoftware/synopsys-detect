/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.projectinspector;

import java.io.File;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class OnlineProjectInspectorResolver implements com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver {
    private final ArtifactoryProjectInspectorInstaller projectInspectorInstaller;
    private final DirectoryManager directoryManager;

    private boolean hasResolvedInspector = false;
    private ExecutableTarget inspector = null;

    public OnlineProjectInspectorResolver(ArtifactoryProjectInspectorInstaller projectInspectorInstaller, DirectoryManager directoryManager) {
        this.projectInspectorInstaller = projectInspectorInstaller;
        this.directoryManager = directoryManager;
    }

    @Override
    public ExecutableTarget resolveProjectInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            File installDirectory = directoryManager.getPermanentDirectory("project-inspector");
            inspector = ExecutableTarget.forFile(projectInspectorInstaller.install(installDirectory));
        }

        return inspector;
    }
}
