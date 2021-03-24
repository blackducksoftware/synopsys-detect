/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.function.ThrowingBiFunction;

public class OnlineNugetInspectorLocator implements NugetInspectorLocator {
    private final NugetInspectorInstaller nugetInspectorInstaller;
    private final DirectoryManager directoryManager;
    @Nullable
    private final String overrideVersion;

    public OnlineNugetInspectorLocator(final NugetInspectorInstaller nugetInspectorInstaller, final DirectoryManager directoryManager, @Nullable final String overrideVersion) {
        this.nugetInspectorInstaller = nugetInspectorInstaller;
        this.directoryManager = directoryManager;
        this.overrideVersion = overrideVersion;
    }

    @Override
    public File locateDotnet3Inspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet3);
    }

    @Override
    public File locateDotnetInspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installDotNet);
    }

    @Override
    public File locateExeInspector() throws DetectableException {
        return locateInspector(nugetInspectorInstaller::installExeInspector);
    }

    private File locateInspector(final ThrowingBiFunction<File, String, File, DetectableException> inspectorInstaller) throws DetectableException {
        try {
            final File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            return inspectorInstaller.apply(nugetDirectory, overrideVersion);
        } catch (final Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }
}
