/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
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

    public DetectFontLoaderFactory(BootSingletons bootSingletons, UtilitySingletons utilitySingletons) {
        airGapInspectorPaths = utilitySingletons.getAirGapInspectorPaths();
        artifactResolver = utilitySingletons.getArtifactResolver();
        directoryManager = bootSingletons.getDirectoryManager();
    }

    public DetectFontLoader detectFontLoader() throws DetectUserFriendlyException {
        DetectFontLocator locator;
        Optional<File> fontAirGapPath = airGapInspectorPaths.getFontsAirGapFile();
        if (fontAirGapPath.isPresent()) {
            locator = new AirGapFontLocator(airGapInspectorPaths);
        } else {
            locator = new OnlineDetectFontLocator(new DetectFontInstaller(artifactResolver), directoryManager);
        }
        return new DetectFontLoader(locator);
    }
}
