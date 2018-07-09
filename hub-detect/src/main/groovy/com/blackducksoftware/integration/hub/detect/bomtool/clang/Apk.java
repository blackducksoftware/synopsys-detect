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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class Apk extends LinuxPackageManager {
    private static final String PKG_MGR_NAME = "apk";
    private static final List<String> VERSION_COMMAND_ARGS = Arrays.asList("--version");
    private static final String APK_INFO_SUBCOMMAND = "info";
    private static final String APK_WHO_OWNS_OPTION = "--who-owns";
    private static final String EXPECTED_TEXT = "apk-tools ";
    private static final List<String> QUERY_ARCH_COMMAND_ARGS = Arrays.asList(APK_INFO_SUBCOMMAND, "--print-arch");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<Forge> forges = Arrays.asList(Forge.ALPINE);
    private String architecture = null;

    @Override
    public String getPkgMgrName() {
        return PKG_MGR_NAME;
    }

    @Override
    public Forge getDefaultForge() {
        return forges.get(0);
    }

    @Override
    public List<Forge> getForges() {
        return forges;
    }

    @Override
    public List<PackageDetails> getDependencyDetails(final ExecutableRunner executableRunner, final Set<File> filesForIScan, final DependencyFile dependencyFile) {
        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        try {
            if (architecture == null) {
                architecture = executableRunner.executeQuietly(PKG_MGR_NAME, QUERY_ARCH_COMMAND_ARGS).getStandardOutput().trim();
                logger.debug(String.format("architecture: %s", architecture));
            }
            final ExecutableOutput queryPackageOutput = executableRunner.executeQuietly(PKG_MGR_NAME, APK_INFO_SUBCOMMAND, APK_WHO_OWNS_OPTION, dependencyFile.getFile().getAbsolutePath());
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
            addToPackageList(dependencyDetailsList, queryPackageOutput.getStandardOutput());
            return dependencyDetailsList;
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s: %s", PKG_MGR_NAME, e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.info(String.format("%s should be scanned by iScan", dependencyFile.getFile().getAbsolutePath()));
                filesForIScan.add(dependencyFile.getFile());
            } else {
                logger.trace(String.format("No point in scanning %s with iScan since it's in the source.dir", dependencyFile.getFile().getAbsolutePath()));
            }
            return dependencyDetailsList;
        }
    }

    private void addToPackageList(final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) {
        final String[] packageLines = queryPackageOutput.split("\n");
        for (final String packageLine : packageLines) {
            if (!valid(packageLine)) {
                logger.debug(String.format("Skipping line: %s", packageLine));
                continue;
            }
            final String[] packageLineParts = packageLine.split("\\s+");
            final String packageNameAndVersion = packageLineParts[4];
            logger.trace(String.format("packageNameAndVersion: %s", packageNameAndVersion));
            final String[] parts = packageNameAndVersion.split("-");
            if (parts.length < 3) {
                logger.error(String.format("apk info output contains an invalid package: %s", packageNameAndVersion));
                continue;
            }
            final String version = String.format("%s-%s", parts[parts.length - 2], parts[parts.length - 1]);
            logger.trace(String.format("version: %s", version));
            final String component = deriveComponent(parts);
            logger.trace(String.format("component: %s", component));
            // if a package starts with a period, we should ignore it because it is a virtual meta package and the version information is missing
            if (!component.startsWith(".")) {
                final String externalId = String.format("%s/%s/%s", component, version, architecture);
                logger.debug(String.format("Constructed externalId: %s", externalId));
                final PackageDetails dependencyDetails = new PackageDetails(component, version, architecture);
                dependencyDetailsList.add(dependencyDetails);
            }
        }
    }

    private String deriveComponent(final String[] parts) {
        String component = "";
        for (int i = 0; i < parts.length - 2; i++) {
            final String part = parts[i];
            if (StringUtils.isNotBlank(component)) {
                component += String.format("-%s", part);
            } else {
                component = part;
            }
        }
        return component;
    }

    @Override
    public List<String> getCheckPresenceCommandArgs() {
        return VERSION_COMMAND_ARGS;
    }

    @Override
    public String getCheckPresenceCommandOutputExpectedText() {
        return EXPECTED_TEXT;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private boolean valid(final String packageLine) {
        return packageLine.contains(" is owned by ");
    }
}
