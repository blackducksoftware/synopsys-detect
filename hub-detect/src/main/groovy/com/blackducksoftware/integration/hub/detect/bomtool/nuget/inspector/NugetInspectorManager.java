/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;

public class NugetInspectorManager {
    private final NugetInspectorInstaller nugetInspectorInstaller;
    private final ExecutableManager executableManager;
    private final DetectConfiguration detectConfiguration;
    private final DetectInfo detectInfo;

    private NugetInspector resolvedNugetInspector = null;
    private DetectFileManager detectFileManager;

    public NugetInspectorManager(final NugetInspectorInstaller nugetInspectorInstaller, final ExecutableManager executableManager,
        final DetectConfiguration detectConfiguration, final DetectInfo detectInfo, final DetectFileManager detectFileManager) {
        this.nugetInspectorInstaller = nugetInspectorInstaller;
        this.executableManager = executableManager;
        this.detectConfiguration = detectConfiguration;
        this.detectInfo = detectInfo;
        this.detectFileManager = detectFileManager;
    }

    public NugetInspector findNugetInspector() throws BomToolException {
        if (resolvedNugetInspector == null) {
            resolvedNugetInspector = resolveNugetInspector();
        }

        return resolvedNugetInspector;
    }

    private NugetInspector resolveNugetInspector() throws BomToolException {
        final String detectSourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH);
        final String nugetPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_PATH);
        final String nugetConfig = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
        final String nugetExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.NUGET, true, new File(detectSourcePath), nugetPath);

        //original inspector
        final String exeInspectorName = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME);
        //new inspector
        final String dotnetInspectorName = "IntegrationNugetInspector";

        String nugetExe = executableManager.getExecutablePathOrOverride(ExecutableType.NUGET, true, new File(detectSourcePath), nugetPath);
        if (StringUtils.isBlank(nugetExe)) {
            throw new BomToolException("Nuget must be on the path.");
        }

        File outputDirectory = detectFileManager.getSharedDirectory("nuget");
        if (shouldForceExeInspector(detectInfo)) { //If they are using windows we may want to use the old inspector, at lease until 5.
            //try to find a nuget.exe
            Optional<File> install = nugetInspectorInstaller.install(exeInspectorName, nugetExe, outputDirectory);
            if (install.isPresent()) {
                File toolsDirectory = new File(install.get(), "tools");
                final String exeName = exeInspectorName + ".exe";
                final File inspectorExe = new File(toolsDirectory, exeName);
                return new ExeNugetInspector(inspectorExe.toString());
            }
        } else {
            //attempt to use the dotnetcore inspector
            String dotnetPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOTNET_PATH);
            String dotnetExe = executableManager.getExecutablePathOrOverride(ExecutableType.DOTNET, true, new File(detectSourcePath), dotnetPath);
            if (StringUtils.isNotBlank(dotnetExe)) {
                Optional<File> install = nugetInspectorInstaller.install(dotnetInspectorName, nugetExecutablePath, outputDirectory);
                if (install.isPresent()) {
                    File toolsDirectory = new File(install.get(), "tools");
                    final String exeName = dotnetInspectorName + ".dll";
                    final File inspectorExe = new File(toolsDirectory, exeName);
                    return new DotNetCoreNugetInspector(dotnetExe, inspectorExe.toString());
                }
            }
        }
        return null;
    }

    private boolean shouldForceExeInspector(DetectInfo detectInfo) {
        if (true) {
            return true;
        }

        if (detectInfo.getCurrentOs() != OperatingSystemType.WINDOWS) {
            return false;
        }
        //if customers have overridden the repo url's and include a v2 api, we must use the old nuget inspector (exe inspector) until 5.0.0 of detect.
        //TODO: Remove in 5.0.0
        for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL)) {
            if (source.contains("v2")) {
                //logger.warn("You are using Version 2 of the Nuget Api. Please update to version 1.");
                return true;
            }
        }
        return false;
    }

}
