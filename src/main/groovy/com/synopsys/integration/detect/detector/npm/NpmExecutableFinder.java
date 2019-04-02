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
package com.synopsys.integration.detect.detector.npm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.type.ExecutableType;
import com.synopsys.integration.detect.util.executable.Executable;
import com.synopsys.integration.detect.util.executable.ExecutableFinder;
import com.synopsys.integration.detect.util.executable.ExecutableRunner;
import com.synopsys.integration.detect.util.executable.ExecutableRunnerException;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class NpmExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(NpmExecutableFinder.class);

    private final DirectoryManager directoryManager;
    private final ExecutableFinder executableFinder;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;

    private String foundNpm = null;
    private boolean hasLookedForNpm = false;

    public NpmExecutableFinder(final DirectoryManager directoryManager, final ExecutableFinder executableFinder, final ExecutableRunner executableRunner,
        final DetectConfiguration detectConfiguration) {
        this.directoryManager = directoryManager;
        this.executableFinder = executableFinder;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
    }

    public String findNpm(final DetectorEnvironment environment) throws DetectorException {
        try {
            if (!hasLookedForNpm) {
                foundNpm = findNpm();
                hasLookedForNpm = true;
            }
            return foundNpm;
        } catch (final Exception e) {
            throw new DetectorException(e);
        }
    }

    String findNpm() {
        final String npm = executableFinder.getExecutablePathOrOverride(ExecutableType.NPM, true, directoryManager.getSourceDirectory(),
            detectConfiguration.getProperty(DetectProperty.DETECT_NPM_PATH, PropertyAuthority.None));
        if (validateNpm(null, npm)) {
            return npm;
        }
        return null;
    }

    boolean validateNpm(final File directoryToSearch, final String npmExePath) {
        if (StringUtils.isNotBlank(npmExePath)) {
            Executable npmVersionExe = null;
            final List<String> arguments = new ArrayList<>();
            arguments.add("-version");

            String npmNodePath = detectConfiguration.getProperty(DetectProperty.DETECT_NPM_NODE_PATH, PropertyAuthority.None);
            if (StringUtils.isNotBlank(npmNodePath)) {
                final int lastSlashIndex = npmNodePath.lastIndexOf("/");
                if (lastSlashIndex >= 0) {
                    npmNodePath = npmNodePath.substring(0, lastSlashIndex);
                }
                final Map<String, String> environmentVariables = new HashMap<>();
                environmentVariables.put("PATH", npmNodePath);

                npmVersionExe = new Executable(directoryToSearch, environmentVariables, npmExePath, arguments);
            } else {
                npmVersionExe = new Executable(directoryToSearch, npmExePath, arguments);
            }
            try {
                final String npmVersion = executableRunner.execute(npmVersionExe).getStandardOutput();
                logger.debug("Npm version " + npmVersion);
                return true;
            } catch (final ExecutableRunnerException e) {
                logger.error("Could not run npm to get the version: " + e.getMessage());
            }
        }
        return false;
    }

}
