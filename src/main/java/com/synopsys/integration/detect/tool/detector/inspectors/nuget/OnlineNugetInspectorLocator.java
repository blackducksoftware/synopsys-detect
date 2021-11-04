/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.tool.cache.InstalledTool;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.function.ThrowingBiFunction;

public class OnlineNugetInspectorLocator implements NugetInspectorLocator {
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
        return locateInspector(nugetInspectorInstaller::installDotNet5, InstalledTool.NUGET_INSPECTOR_5);
    }

    @Override
    public File locateDotnet3Inspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet3, InstalledTool.NUGET_INSPECTOR_3);
    }

    @Override
    public File locateDotnetInspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet, InstalledTool.NUGET_INSPECTOR);
    }

    @Override
    public File locateExeInspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installExeInspector, InstalledTool.NUGET_INSPECTOR_EXE);
    }

    private File locateInspector(ThrowingBiFunction<File, String, File, DetectableException> inspectorInstaller, InstalledTool inspectorType) throws DetectableException {
        File inspector;
        Optional<File> cachedInstall = installedToolLocator.locateTool(inspectorType);
        try {
            File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            inspector = inspectorInstaller.apply(nugetDirectory, overrideVersion);
        } catch (Exception e) {
            if (cachedInstall.isPresent()) {
                return cachedInstall.get();
            }
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
        if (inspector == null) {
            if (cachedInstall.isPresent()) {
                return cachedInstall.get();
            }
        } else {
            installedToolManager.saveInstalledToolLocation(inspectorType, inspector.getAbsolutePath());
        }
        return inspector;
    }
}
