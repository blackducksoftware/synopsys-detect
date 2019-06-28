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
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class OnlineNugetInspectorLocator implements NugetInspectorLocator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NugetInspectorInstaller nugetInspectorInstaller;
    private final DirectoryManager directoryManager;
    private final String overrideVersion;

    public OnlineNugetInspectorLocator(final NugetInspectorInstaller nugetInspectorInstaller, final DirectoryManager directoryManager, final String overrideVersion) {
        this.nugetInspectorInstaller = nugetInspectorInstaller;
        this.directoryManager = directoryManager;
        this.overrideVersion = overrideVersion;
    }

    @Override
    public File locateDotnetInspector() throws DetectableException {
        try {
            logger.info("Will attempt to resolve the dotnet inspector version.");
            File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            return nugetInspectorInstaller.installDotNet(nugetDirectory, Optional.of(overrideVersion));
        } catch (Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }

    @Override
    public File locateExeInspector() throws DetectableException {
        try {
            File nugetDirectory = directoryManager.getPermanentDirectory("nuget");
            logger.info("Will attempt to resolve the classic inspector version.");
            return nugetInspectorInstaller.installExeInspector(nugetDirectory, Optional.of(overrideVersion));
        } catch (Exception e) {
            throw new DetectableException("Unable to install the nuget inspector from Artifactory.", e);
        }
    }

}
