/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;

import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class AirgapNugetInspectorLocator implements NugetInspectorLocator {
    public static final String INSPECTOR_DIR_DOTNET3 = "nuget_dotnet3";
    public static final String INSPECTOR_DIR_DOTNET5 = "nuget_dotnet5";
    public static final String INSPECTOR_DIR_DOTNET = "nuget_dotnet";
    public static final String INSPECTOR_DIR_CLASSIC = "nuget_classic";

    private final AirGapInspectorPaths airGapInspectorPaths;

    public AirgapNugetInspectorLocator(final AirGapInspectorPaths airGapInspectorPaths) {
        this.airGapInspectorPaths = airGapInspectorPaths;
    }

    @Override
    public File locateDotnet3Inspector() throws DetectableException {
        return locateInspector(INSPECTOR_DIR_DOTNET3);
    }

    @Override
    public File locateDotnet5Inspector() throws DetectableException {
        return locateInspector(INSPECTOR_DIR_DOTNET5);
    }

    @Override
    public File locateDotnetInspector() throws DetectableException {
        return locateInspector(INSPECTOR_DIR_DOTNET);
    }

    @Override
    public File locateExeInspector() throws DetectableException {
        return locateInspector(INSPECTOR_DIR_CLASSIC);
    }

    private File locateInspector(final String childName) throws DetectableException {
        return airGapInspectorPaths.getNugetInspectorAirGapFile()
                   .map(nugetAirGapPath -> new File(nugetAirGapPath, childName))
                   .orElseThrow(() -> new DetectableException("Could not get the nuget air gap path"));
    }
}
