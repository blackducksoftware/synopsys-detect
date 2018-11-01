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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolException;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class GoInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(GoInspectorManager.class);

    private final DirectoryManager directoryManager;
    private final ExecutableFinder executableFinder;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;

    private boolean hasResolvedInspector;
    private String resolvedGoDep;

    public GoInspectorManager(final DirectoryManager directoryManager, final ExecutableFinder executableFinder, final ExecutableRunner executableRunner,
        final DetectConfiguration detectConfiguration) {
        this.directoryManager = directoryManager;
        this.executableFinder = executableFinder;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
    }

    public String evaluate() throws BomToolException {
        try {
            if (!hasResolvedInspector) {
                resolvedGoDep = install();
            }

            return resolvedGoDep;
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

    public String install() throws ExecutableRunnerException {
        String goDepPath = detectConfiguration.getProperty(DetectProperty.DETECT_GO_DEP_PATH, PropertyAuthority.None);
        if (StringUtils.isBlank(goDepPath)) {
            final File goDep = getGoDepInstallLocation();
            if (goDep.exists()) {
                goDepPath = goDep.getAbsolutePath();
            } else {
                goDepPath = executableFinder.getExecutablePath(ExecutableType.GO_DEP, true, directoryManager.getSourceDirectory().getAbsolutePath());
            }
        }
        if (StringUtils.isBlank(goDepPath)) {
            final String goExecutable = executableFinder.getExecutablePath(ExecutableType.GO, true, directoryManager.getSourceDirectory().getAbsolutePath());
            goDepPath = installGoDep(goExecutable);
        }
        return goDepPath;
    }

    private String installGoDep(final String goExecutable) throws ExecutableRunnerException {
        final File goDep = getGoDepInstallLocation();
        final File installDirectory = goDep.getParentFile();
        installDirectory.mkdirs();
        logger.debug("Retrieving the Go Dep tool");

        final Executable getGoDep = new Executable(installDirectory, goExecutable, Arrays.asList(
            "get",
            "-u",
            "-v",
            "-d",
            "github.com/golang/dep/cmd/dep"));
        executableRunner.execute(getGoDep);

        logger.debug("Building the Go Dep tool in " + installDirectory.getAbsolutePath());
        final Executable buildGoDep = new Executable(installDirectory, goExecutable, Arrays.asList(
            "build",
            "github.com/golang/dep/cmd/dep"));
        executableRunner.execute(buildGoDep);

        return goDep.getAbsolutePath();
    }

    private File getGoDepInstallLocation() {
        final File goOutputDirectory = directoryManager.getSharedDirectory("go");
        return new File(goOutputDirectory, executableFinder.getExecutableName(ExecutableType.GO_DEP));
    }
}
