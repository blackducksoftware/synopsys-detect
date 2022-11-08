package com.synopsys.integration.detect.tool.detector.inspector.nuget;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.util.OperatingSystemType;

public class AirgapNugetInspectorResolver implements NugetInspectorResolver {
    public static final String LINUX_DIR = "linux";
    public static final String WINDOWS_DIR = "windows";
    public static final String MAC_DIR = "macosx";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final NugetInspectorExecutableLocator nugetInspectorExecutableLocator;
    private final DetectInfo detectInfo;

    private boolean hasResolvedInspector;
    private ExecutableTarget inspector = null;

    public AirgapNugetInspectorResolver(AirGapInspectorPaths airGapInspectorPaths, NugetInspectorExecutableLocator nugetInspectorExecutableLocator, DetectInfo detectInfo) {
        this.airGapInspectorPaths = airGapInspectorPaths;
        this.nugetInspectorExecutableLocator = nugetInspectorExecutableLocator;
        this.detectInfo = detectInfo;
    }

    @Override
    public ExecutableTarget resolveNugetInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            inspector = resolve();
        }
        return inspector;
    }

    private ExecutableTarget resolve() throws DetectableException {
        if (airGapInspectorPaths.getNugetInspectorAirGapFile().isPresent()) {
            File airgapPath = airGapInspectorPaths.getNugetInspectorAirGapFile().get();
            File platformPath = new File(airgapPath, determinePlatformDirectory());
            return ExecutableTarget.forFile(nugetInspectorExecutableLocator.findExecutable(platformPath));
        } else {
            logger.debug("Could not locate project inspector executable in Air Gap zip.");
            return null;
        }
    }

    private String determinePlatformDirectory() {
        if (detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS) {
            return WINDOWS_DIR;
        } else if (detectInfo.getCurrentOs() == OperatingSystemType.MAC) {
            return MAC_DIR;
        } else {
            return LINUX_DIR;
        }
    }
}
