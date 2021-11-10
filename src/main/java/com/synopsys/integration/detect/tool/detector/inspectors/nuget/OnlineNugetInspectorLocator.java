/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.function.ThrowingBiFunction;

public class OnlineNugetInspectorLocator implements NugetInspectorLocator {
    private static final String NUGET_INSPECTOR_INSTALLED_TOOL_JSON_KEY = "nuget-inspector";
    private static final String NUGET_INSPECTOR_5_INSTALLED_TOOL_JSON_KEY = "nuget-inspector5";
    private static final String NUGET_INSPECTOR_3_INSTALLED_TOOL_JSON_KEY = "nuget-inspector3";
    private static final String NUGET_INSPECTOR_EXE_INSTALLED_TOOL_JSON_KEY = "nuget-inspector-exe";

    private final NugetInspectorInstaller nugetInspectorInstaller;
    private final DirectoryManager directoryManager;
    @Nullable
    private final String overrideVersion;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    public OnlineNugetInspectorLocator(NugetInspectorInstaller nugetInspectorInstaller, DirectoryManager directoryManager, @Nullable String overrideVersion,
        InstalledToolManager installedToolManager, InstalledToolLocator installedToolLocator) {
        this.nugetInspectorInstaller = nugetInspectorInstaller;
        this.directoryManager = directoryManager;
        this.overrideVersion = overrideVersion;
        this.installedToolManager = installedToolManager;
        this.installedToolLocator = installedToolLocator;
    }

    @Override
    public File locateDotnet5Inspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet5, NUGET_INSPECTOR_5_INSTALLED_TOOL_JSON_KEY);
    }

    @Override
    public File locateDotnet3Inspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet3, NUGET_INSPECTOR_3_INSTALLED_TOOL_JSON_KEY);
    }

    @Override
    public File locateDotnetInspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet, NUGET_INSPECTOR_INSTALLED_TOOL_JSON_KEY);
    }

    @Override
    public File locateExeInspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installExeInspector, NUGET_INSPECTOR_EXE_INSTALLED_TOOL_JSON_KEY);
    }

    private File locateInspector(ThrowingBiFunction<File, String, File, DetectableException> inspectorInstaller, String inspectorKey) throws DetectableException {
        try {
            File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            File inspector = inspectorInstaller.apply(nugetDirectory, overrideVersion);
            installedToolManager.saveInstalledToolLocation(inspectorKey, inspector.getAbsolutePath());
            return inspector;
        } catch (Exception e) {
            return installedToolLocator.locateTool(inspectorKey).orElseThrow(() ->
                new DetectableException("Unable to install the nuget inspector from Artifactory.", e));
        }
    }
}
