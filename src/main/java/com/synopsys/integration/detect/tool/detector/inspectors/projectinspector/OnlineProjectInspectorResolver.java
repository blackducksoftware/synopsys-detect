package com.synopsys.integration.detect.tool.detector.inspectors.projectinspector;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class OnlineProjectInspectorResolver implements com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver {
    private final ArtifactoryProjectInspectorInstaller projectInspectorInstaller;

    private boolean hasResolvedInspector;
    private ExecutableTarget inspector = null;

    public OnlineProjectInspectorResolver(ArtifactoryProjectInspectorInstaller projectInspectorInstaller) {
        this.projectInspectorInstaller = projectInspectorInstaller;
    }

    @Override
    public ExecutableTarget resolveProjectInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            inspector = ExecutableTarget.forFile(projectInspectorInstaller.install());
        }

        return inspector;
    }
}
