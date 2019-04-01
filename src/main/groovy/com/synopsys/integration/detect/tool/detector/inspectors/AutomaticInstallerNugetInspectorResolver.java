/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableResolver;
import com.synopsys.integration.detect.type.OperatingSystemType;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.DotNetCoreNugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.impl.ExeNugetInspector;
import com.synopsys.integration.exception.IntegrationException;

public abstract class AutomaticInstallerNugetInspectorResolver implements NugetInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectExecutableResolver executableResolver;
    private final ExecutableRunner executableRunner;
    private final DetectInfo detectInfo;
    private final FileFinder fileFinder;
    protected final NugetInspectorOptions nugetInspectorOptions;

    private boolean hasResolvedInspector;
    private NugetInspector resolvedNugetInspector;

    public AutomaticInstallerNugetInspectorResolver(final DetectExecutableResolver executableResolver, final ExecutableRunner executableRunner, final DetectInfo detectInfo,
        final FileFinder fileFinder, final NugetInspectorOptions nugetInspectorOptions) {
        this.executableResolver = executableResolver;
        this.executableRunner = executableRunner;
        this.detectInfo = detectInfo;
        this.fileFinder = fileFinder;
        this.nugetInspectorOptions = nugetInspectorOptions;
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

    //Return the File of the nuget inspector nupkg (unzipped).
    public abstract File installExeInspector() throws DetectableException;

    //Return the File of the nuget inspector nupkg (unzipped).
    public abstract File installDotnetInspector() throws DetectableException;

    private NugetInspector install() throws DetectUserFriendlyException, IntegrationException, IOException {
        //dotnet
        final File dotnetExecutable = executableResolver.resolveDotNet();

        boolean useDotnet = true;
        if (shouldForceExeInspector(detectInfo)) {
            logger.info("Will use the classic inspector.");
            useDotnet = false;
        } else if (dotnetExecutable == null) {
            if (isNotWindows(detectInfo)) {
                throw new DetectableException("When not on Windows, the nuget inspector requires the dotnet executable to run.");
            } else {
                useDotnet = false;
            }
        }

        if (useDotnet){
            final File dotnetFolder = installDotnetInspector();
            return findDotnetCoreInspector(dotnetFolder, dotnetExecutable);
        } else {
            final File classicFolder = installExeInspector();
            return findExeInspector(classicFolder);
        }
    }

    private DotNetCoreNugetInspector findDotnetCoreInspector(final File nupkgFolder, final File dotnetExecutable) throws DetectableException {
        //new inspector
        final String dotnetInspectorName = "BlackduckNugetInspector.dll";
        logger.info("Searching for: " + dotnetInspectorName);
        final File toolsFolder = new File(nupkgFolder, "tools");
        final Optional<File> foundExe = fileFinder.findFiles(toolsFolder, dotnetInspectorName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            final String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new DotNetCoreNugetInspector(dotnetExecutable, inspectorExe, executableRunner);
        } else {
            throw new DetectableException("Unable to find nuget inspector, looking for " + dotnetInspectorName + " in " + toolsFolder.toString());
        }
    }

    private ExeNugetInspector findExeInspector(final File nupkgFolder) throws DetectableException {
        //original inspector
        final String exeName = nugetInspectorOptions.getNugetInspectorName() + ".exe";
        logger.info("Searching for: " + exeName);
        final File toolsFolder = new File(nupkgFolder, "tools");
        logger.debug("Searching in: " + toolsFolder.getAbsolutePath());
        final Optional<File> foundExe = fileFinder.findFiles(toolsFolder, exeName, 3).stream().findFirst();
        if (foundExe.isPresent() && foundExe.get().exists()) {
            final String inspectorExe = foundExe.get().toString();
            logger.info("Found nuget inspector: " + inspectorExe);
            return new ExeNugetInspector(executableRunner, inspectorExe);
        } else {
            throw new DetectableException("Unable to find nuget inspector named '" + exeName + "' in " + toolsFolder.getAbsolutePath());
        }
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
        //TODO: Remove in 6.0.0
        for (final String source : nugetInspectorOptions.getPackagesRepoUrl()) {
            if (source.contains("v2")) {
                logger.warn("You are using Version 2 of the Nuget Api. Please update to version 3. Support for 2 is deprecated.");
                return true;
            }
        }
        return false;
    }
}
