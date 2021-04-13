/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeManager;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.DotNetCoreNugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.ExeNugetInspector;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.OperatingSystemType;

public class LocatorNugetInspectorResolver implements NugetInspectorResolver {
    private static final String INTEGRATION_NUGET_INSPECTOR_NAME = "IntegrationNugetInspector";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectExecutableResolver executableResolver;
    private final DetectableExecutableRunner executableRunner;
    private final DetectInfo detectInfo;
    private final FileFinder fileFinder;
    private final List<String> packagesRepoUrl;
    private final NugetInspectorLocator nugetInspectorLocator;
    private final DotNetRuntimeManager dotNetRuntimeManager;

    private boolean hasResolvedInspector;
    private NugetInspector resolvedNugetInspector;

    public LocatorNugetInspectorResolver(DetectExecutableResolver executableResolver, DetectableExecutableRunner executableRunner, DetectInfo detectInfo,
        FileFinder fileFinder, List<String> packagesRepoUrl, NugetInspectorLocator nugetInspectorLocator,
        DotNetRuntimeManager dotNetRuntimeManager) {
        this.executableResolver = executableResolver;
        this.executableRunner = executableRunner;
        this.detectInfo = detectInfo;
        this.fileFinder = fileFinder;
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetInspectorLocator = nugetInspectorLocator;
        this.dotNetRuntimeManager = dotNetRuntimeManager;
    }

    @Override
    public NugetInspector resolveNugetInspector() throws DetectableException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedNugetInspector = install();
            }

            return resolvedNugetInspector;
        } catch (Exception e) {
            throw new DetectableException(e);
        }
    }

    private NugetInspector install() throws IntegrationException {
        //dotnet
        ExecutableTarget dotnetExecutable = executableResolver.resolveDotNet();

        boolean useDotnet = true;
        if (shouldForceExeInspector(detectInfo)) {
            logger.debug("Will use the classic inspector.");
            useDotnet = false;
        } else if (dotnetExecutable == null) {
            if (isNotWindows(detectInfo)) {
                throw new DetectableException("When not on Windows, the nuget inspector requires the dotnet executable to run.");
            } else {
                useDotnet = false;
            }
        }

        if (useDotnet) {
            File dotnetFolder;
            if (dotNetRuntimeManager.isRuntimeAvailable(3, 1)) {
                dotnetFolder = nugetInspectorLocator.locateDotnet3Inspector();
                return findDotnetCoreInspector(dotnetFolder, dotnetExecutable, "NugetDotnet3Inspector.dll");
            } else {
                dotnetFolder = nugetInspectorLocator.locateDotnetInspector();
                return findDotnetCoreInspector(dotnetFolder, dotnetExecutable, "BlackduckNugetInspector.dll");
            }
        } else {
            File classicFolder = nugetInspectorLocator.locateExeInspector();
            return findExeInspector(classicFolder);
        }
    }

    private NugetInspector findDotnetCoreInspector(File nupkgFolder, ExecutableTarget dotnetExecutable, String dotnetInspectorName) throws DetectableException {
        Function<String, NugetInspector> constructor = (String exePath) -> new DotNetCoreNugetInspector(dotnetExecutable, exePath, executableRunner);
        return findInspector(nupkgFolder, dotnetInspectorName, constructor);
    }

    //original inspector
    private NugetInspector findExeInspector(File nupkgFolder) throws DetectableException {
        String exeName = INTEGRATION_NUGET_INSPECTOR_NAME + ".exe";
        Function<String, NugetInspector> constructor = (String exePath) -> new ExeNugetInspector(executableRunner, exePath);
        return findInspector(nupkgFolder, exeName, constructor);
    }

    private NugetInspector findInspector(File nupkgFolder, String inspectorName, Function<String, NugetInspector> inspectorInitializer) throws DetectableException {
        logger.debug("Searching for: " + inspectorName);
        File toolsFolder = new File(nupkgFolder, "tools");
        logger.debug("Searching in: " + toolsFolder.getAbsolutePath());
        File foundExecutable = fileFinder.findFiles(toolsFolder, inspectorName, 3)
                                   .stream()
                                   .findFirst()
                                   .filter(File::exists)
                                   .orElseThrow(() -> new DetectableException(String.format("Unable to find nuget inspector, looking for %s in %s", inspectorName, toolsFolder.toString())));
        String inspectorExecutable = foundExecutable.getAbsolutePath();
        logger.debug("Found nuget inspector: {}", inspectorExecutable);
        return inspectorInitializer.apply(inspectorExecutable);
    }

    private boolean isWindows(DetectInfo detectInfo) {
        return detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS;
    }

    private boolean isNotWindows(DetectInfo detectInfo) {
        return !isWindows(detectInfo);
    }

    private boolean shouldForceExeInspector(DetectInfo detectInfo) {
        if (isNotWindows(detectInfo)) {
            return false;
        }

        //if customers have overridden the repo url's and include a v2 api, we must use the old nuget inspector (exe inspector) until 5.0.0 of detect.
        //TODO: Remove in 7.0.0
        for (String source : packagesRepoUrl) {
            if (source.contains("v2")) {
                logger.warn("You are using Version 2 of the Nuget Api. Please update to version 3. Support for 2 is deprecated.");
                return true;
            }
        }
        return false;
    }
}
