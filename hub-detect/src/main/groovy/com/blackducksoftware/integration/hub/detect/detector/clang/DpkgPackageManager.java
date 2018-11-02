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
package com.blackducksoftware.integration.hub.detect.detector.clang;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.synopsys.integration.hub.bdio.model.Forge;

public class DpkgPackageManager extends ClangLinuxPackageManager {
    private static final String PKG_MGR_NAME = "dpkg";
    private static final List<String> VERSION_COMMAND_ARGS = Arrays.asList("--version");
    private static final String VERSION_OUTPUT_EXPECTED_TEXT = "package management program version";
    private static final String WHO_OWNS_OPTION = "-S";
    private static final String GET_PKG_INFO_OPTION = "-s";
    private static final Logger logger = LoggerFactory.getLogger(DpkgPackageManager.class);

    public DpkgPackageManager() {
        super(logger, PKG_MGR_NAME, PKG_MGR_NAME, Arrays.asList(Forge.UBUNTU, Forge.DEBIAN), VERSION_COMMAND_ARGS,
            VERSION_OUTPUT_EXPECTED_TEXT, Arrays.asList(WHO_OWNS_OPTION));
    }

    @Override
    protected void addToPackageList(final ExecutableRunner executableRunner, File workingDirectory, final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) {
        final String[] packageLines = queryPackageOutput.split("\n");
        for (final String packageLine : packageLines) {
            if (!valid(packageLine)) {
                logger.debug(String.format("Skipping line: %s", packageLine));
                continue;
            }
            final String[] queryPackageOutputParts = packageLine.split("\\s+");
            final String[] packageNameArchParts = queryPackageOutputParts[0].split(":");
            final String packageName = packageNameArchParts[0];
            final String packageArch = packageNameArchParts[1];
            logger.debug(String.format("package name: %s; arch: %s", packageName, packageArch));
            final Optional<String> packageVersion = getPackageVersion(executableRunner, workingDirectory, packageName);
            final PackageDetails dependencyDetails = new PackageDetails(packageName, packageVersion.orElse(null), packageArch);
            dependencyDetailsList.add(dependencyDetails);
        }
    }

    @Override
    public Forge getDefaultForge() {
        return Forge.UBUNTU;
    }

    private boolean valid(final String packageLine) {
        return packageLine.matches(".+:.+: .+");
    }

    private Optional<String> getPackageVersion(final ExecutableRunner executableRunner, File workingDirectory, final String packageName) {
        try {
            final ExecutableOutput packageStatusOutput = executableRunner.executeQuietly(workingDirectory, PKG_MGR_NAME, GET_PKG_INFO_OPTION, packageName);
            logger.debug(String.format("packageStatusOutput: %s", packageStatusOutput));
            return getPackageVersionFromStatusOutput(packageName, packageStatusOutput.getStandardOutput());
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s to get package info: %s", PKG_MGR_NAME, e.getMessage()));
        }
        return Optional.empty();
    }

    private Optional<String> getPackageVersionFromStatusOutput(final String packageName, final String packageStatusOutput) {
        final String[] packageStatusOutputLines = packageStatusOutput.split("\\n");
        for (final String packageStatusOutputLine : packageStatusOutputLines) {
            final String[] packageStatusOutputLineNameValue = packageStatusOutputLine.split(":\\s+");
            final String label = packageStatusOutputLineNameValue[0];
            final String value = packageStatusOutputLineNameValue[1];
            if ("Status".equals(label.trim()) && !value.contains("installed")) {
                logger.debug(String.format("%s is not installed; Status is: %s", packageName, value));
                return Optional.empty();
            }
            if ("Version".equals(label)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
