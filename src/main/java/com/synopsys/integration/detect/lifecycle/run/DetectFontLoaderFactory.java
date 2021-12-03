package com.synopsys.integration.detect.lifecycle.run;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.blackduck.DetectFontLoader;
import com.synopsys.integration.detect.workflow.blackduck.font.AirGapFontLocator;
import com.synopsys.integration.detect.workflow.blackduck.font.DetectFontInstaller;
import com.synopsys.integration.detect.workflow.blackduck.font.DetectFontLocator;
import com.synopsys.integration.detect.workflow.blackduck.font.OnlineDetectFontLocator;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class DetectFontLoaderFactory {
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final ArtifactResolver artifactResolver;
    private final DirectoryManager directoryManager;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    public DetectFontLoaderFactory(BootSingletons bootSingletons, UtilitySingletons utilitySingletons) {
        airGapInspectorPaths = utilitySingletons.getAirGapInspectorPaths();
        artifactResolver = utilitySingletons.getArtifactResolver();
        directoryManager = bootSingletons.getDirectoryManager();
        installedToolManager = bootSingletons.getInstalledToolManager();
        installedToolLocator = bootSingletons.getInstalledToolLocator();
    }

    public DetectFontLoader detectFontLoader() {
        DetectFontLocator locator;
        Optional<File> fontAirGapPath = airGapInspectorPaths.getFontsAirGapDirectory();
        if (fontAirGapPath.isPresent()) {
            locator = new AirGapFontLocator(airGapInspectorPaths);
        } else {
            locator = new OnlineDetectFontLocator(new DetectFontInstaller(artifactResolver, installedToolManager, installedToolLocator), directoryManager);
        }
        return new DetectFontLoader(locator);
    }
}
