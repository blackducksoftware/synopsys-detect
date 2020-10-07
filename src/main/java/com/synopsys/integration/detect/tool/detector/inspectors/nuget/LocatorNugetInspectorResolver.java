/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeManager;
import com.synopsys.integration.detect.type.OperatingSystemType;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.DotNetCoreNugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.ExeNugetInspector;
import com.synopsys.integration.exception.IntegrationException;

public class LocatorNugetInspectorResolver implements NugetInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectExecutableResolver executableResolver;
    private final ExecutableRunner executableRunner;
    private final DetectInfo detectInfo;
    private final FileFinder fileFinder;
    private final String nugetInspectorName;
    private final List<String> packagesRepoUrl;
    private final NugetInspectorLocator nugetInspectorLocator;
    private final DotNetRuntimeManager dotNetRuntimeManager;

    private boolean hasResolvedInspector;
    private NugetInspector resolvedNugetInspector;

    public LocatorNugetInspectorResolver(final DetectExecutableResolver executableResolver, final ExecutableRunner executableRunner, final DetectInfo detectInfo,
        final FileFinder fileFinder, final String nugetInspectorName, final List<String> packagesRepoUrl, final NugetInspectorLocator nugetInspectorLocator,
        final DotNetRuntimeManager dotNetRuntimeManager) {
        this.executableResolver = executableResolver;
        this.executableRunner = executableRunner;
        this.detectInfo = detectInfo;
        this.fileFinder = fileFinder;
        this.nugetInspectorName = nugetInspectorName;
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
        } catch (final Exception e) {
            throw new DetectableException(e);
        }
    }

    private NugetInspector install() throws IntegrationException {
        //dotnet
        final File dotnetExecutable = executableResolver.resolveDotNet();

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
            final File dotnetFolder;
            if (dotNetRuntimeManager.isRuntimeAvailable(3, 1)) {
                dotnetFolder = nugetInspectorLocator.locateDotnet3Inspector();
                return findDotnetCoreInspector(dotnetFolder, dotnetExecutable, "NugetDotnet3Inspector.dll");
            } else {
                dotnetFolder = nugetInspectorLocator.locateDotnetInspector();
                return findDotnetCoreInspector(dotnetFolder, dotnetExecutable, "BlackduckNugetInspector.dll");
            }
        } else {
            final File classicFolder = nugetInspectorLocator.locateExeInspector();
            return findExeInspector(classicFolder);
        }
    }

    private NugetInspector findDotnetCoreInspector(final File nupkgFolder, final File dotnetExecutable, final String dotnetInspectorName) throws DetectableException {
        final Function<String, NugetInspector> constructor = (String exePath) -> new DotNetCoreNugetInspector(dotnetExecutable, exePath, executableRunner);
        return findInspector(nupkgFolder, dotnetInspectorName, constructor);
    }

    //original inspector
    private NugetInspector findExeInspector(final File nupkgFolder) throws DetectableException {
        final String exeName = nugetInspectorName + ".exe";
        final Function<String, NugetInspector> constructor = (String exePath) -> new ExeNugetInspector(executableRunner, exePath);
        return findInspector(nupkgFolder, exeName, constructor);
    }

    private NugetInspector findInspector(final File nupkgFolder, final String inspectorName, final Function<String, NugetInspector> inspectorInitializer) throws DetectableException {
        logger.debug("Searching for: " + inspectorName);
        final File toolsFolder = new File(nupkgFolder, "tools");
        logger.debug("Searching in: " + toolsFolder.getAbsolutePath());
        final File foundExecutable = fileFinder.findFiles(toolsFolder, inspectorName, 3)
                                         .stream()
                                         .findFirst()
                                         .filter(File::exists)
                                         .orElseThrow(() -> new DetectableException(String.format("Unable to find nuget inspector, looking for %s in %s", inspectorName, toolsFolder.toString())));
        final String inspectorExecutable = foundExecutable.getAbsolutePath();
        logger.debug("Found nuget inspector: {}", inspectorExecutable);
        return inspectorInitializer.apply(inspectorExecutable);
    }

    private boolean isWindows(final DetectInfo detectInfo) {
        return detectInfo.getCurrentOs() == OperatingSystemType.WINDOWS;
    }

    private boolean isNotWindows(final DetectInfo detectInfo) {
        return !isWindows(detectInfo);
    }

    private boolean shouldForceExeInspector(final DetectInfo detectInfo) {
        if (isNotWindows(detectInfo)) {
            return false;
        }

        //if customers have overridden the repo url's and include a v2 api, we must use the old nuget inspector (exe inspector) until 5.0.0 of detect.
        //TODO: Remove in 7.0.0
        for (final String source : packagesRepoUrl) {
            if (source.contains("v2")) {
                logger.warn("You are using Version 2 of the Nuget Api. Please update to version 3. Support for 2 is deprecated.");
                return true;
            }
        }
        return false;
    }
}
