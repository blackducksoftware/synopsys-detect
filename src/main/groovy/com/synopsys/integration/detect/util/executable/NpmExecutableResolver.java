/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.util.executable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirectoryManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;

public class NpmExecutableResolver implements NpmResolver {
    public static final String NPM_EXECUTABLE_NAME = "npm";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NpmExecutableResolverOptions npmExecutableResolverOptions;
    private final DirectoryManager directoryManager;
    private final SimpleLocalExecutableFinder simpleLocalExecutableFinder;
    private final SimpleSystemExecutableFinder simpleSystemExecutableFinder;
    private final ExecutableRunner executableRunner;

    public NpmExecutableResolver(final NpmExecutableResolverOptions npmExecutableResolverOptions, final DirectoryManager directoryManager, final SimpleLocalExecutableFinder simpleLocalExecutableFinder,
        final SimpleSystemExecutableFinder simpleSystemExecutableFinder, final ExecutableRunner executableRunner) {
        this.npmExecutableResolverOptions = npmExecutableResolverOptions;
        this.directoryManager = directoryManager;
        this.simpleLocalExecutableFinder = simpleLocalExecutableFinder;
        this.simpleSystemExecutableFinder = simpleSystemExecutableFinder;
        this.executableRunner = executableRunner;
    }

    @Override
    public File resolveNpm(final DetectableEnvironment detectableEnvironment) {
        final String overridePath = npmExecutableResolverOptions.getNpmPath();

        final File npm;
        if (StringUtils.isNotBlank(overridePath)) {
            final File npmNodePath = new File(npmExecutableResolverOptions.getNpmPath());
            npm = simpleLocalExecutableFinder.findExecutable(NPM_EXECUTABLE_NAME, npmNodePath);
        } else {
            npm = simpleSystemExecutableFinder.findExecutable(NPM_EXECUTABLE_NAME);
        }

        if (validateNpm(null, npm)) {
            return npm;
        }
        return null;
    }

    boolean validateNpm(final File directoryToSearch, final File npmExe) {
        if (npmExe != null) {
            Executable npmVersionExe = null;
            final List<String> arguments = new ArrayList<>();
            arguments.add("-version");

            String npmNodePath = npmExecutableResolverOptions.getNpmNodePath();
            if (StringUtils.isNotBlank(npmNodePath)) {
                final int lastSlashIndex = npmNodePath.lastIndexOf("/");
                if (lastSlashIndex >= 0) {
                    npmNodePath = npmNodePath.substring(0, lastSlashIndex);
                }
                final Map<String, String> environmentVariables = new HashMap<>();
                environmentVariables.put("PATH", npmNodePath);

                npmVersionExe = new Executable(directoryToSearch, environmentVariables, npmExe.getAbsolutePath(), arguments);
            } else {
                npmVersionExe = new Executable(directoryToSearch, null, npmExe.getAbsolutePath(), arguments);
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
