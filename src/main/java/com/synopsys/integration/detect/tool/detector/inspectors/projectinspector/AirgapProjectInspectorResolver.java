package com.synopsys.integration.detect.tool.detector.inspectors.projectinspector;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;

public class AirgapProjectInspectorResolver implements ProjectInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final ProjectInspectorExecutableLocator projectInspectorExecutableLocator;

    private boolean hasResolvedInspector;
    private ExecutableTarget inspector = null;

    public AirgapProjectInspectorResolver(AirGapInspectorPaths airGapInspectorPaths, ProjectInspectorExecutableLocator projectInspectorExecutableLocator) {
        this.airGapInspectorPaths = airGapInspectorPaths;
        this.projectInspectorExecutableLocator = projectInspectorExecutableLocator;
    }

    @Override
    public ExecutableTarget resolveProjectInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            resolve();
        }
        return inspector;
    }

    private void resolve() throws DetectableException {
        if (airGapInspectorPaths.getProjectInspectorAirGapFile().isPresent()) {
            File executable = projectInspectorExecutableLocator.findExecutable(airGapInspectorPaths.getProjectInspectorAirGapFile().get());
            inspector = ExecutableTarget.forFile(executable);
        } else {
            logger.debug("Could not locate project inspector executable in Air Gap zip.");
            inspector = null;
        }
    }
}
