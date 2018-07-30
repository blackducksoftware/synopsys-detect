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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class ApkPackageManager extends LinuxPackageManager {
    private static final String PKG_MGR_NAME = "apk";
    private static final List<String> VERSION_COMMAND_ARGS = Arrays.asList("--version");
    private static final String VERSION_OUTPUT_EXPECTED_TEXT = "apk-tools ";
    private static final String INFO_SUBCOMMAND = "info";
    private static final String WHO_OWNS_OPTION = "--who-owns";
    private static final String GET_ARCHITECTURE_OPTION = "--print-arch";
    private static final Logger logger = LoggerFactory.getLogger(ApkPackageManager.class);
    private String architecture = null;

    public ApkPackageManager() {
        super(logger, PKG_MGR_NAME, PKG_MGR_NAME, Arrays.asList(Forge.ALPINE), VERSION_COMMAND_ARGS,
                VERSION_OUTPUT_EXPECTED_TEXT, Arrays.asList(INFO_SUBCOMMAND, WHO_OWNS_OPTION));
    }

    @Override
    protected void addToPackageList(final ExecutableRunner executableRunner, final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) throws ExecutableRunnerException {
        if (architecture == null) {
            architecture = executableRunner.executeQuietly(PKG_MGR_NAME, INFO_SUBCOMMAND, GET_ARCHITECTURE_OPTION).getStandardOutput().trim();
            logger.debug(String.format("architecture: %s", architecture));
        }
        final String[] packageLines = queryPackageOutput.split("\n");
        for (final String packageLine : packageLines) {
            final Optional<List<String>> pkgNameVersionParts = parseIsOwnedByOutputLine(packageLine);
            if (pkgNameVersionParts.isPresent()) {
                final String version = deriveVersion(pkgNameVersionParts.get());
                logger.trace(String.format("version: %s", version));
                final Optional<String> component = deriveComponent(pkgNameVersionParts.get());
                logger.trace(String.format("component: %s", component));
                if (component.isPresent()) {
                    final String externalId = String.format("%s/%s/%s", component, version, architecture);
                    logger.debug(String.format("Constructed externalId: %s", externalId));
                    final PackageDetails dependencyDetails = new PackageDetails(component.get(), version, architecture);
                    dependencyDetailsList.add(dependencyDetails);
                }
            }
        }
    }

    private String deriveVersion(final List<String> pkgParts) {
        return String.format("%s-%s", pkgParts.get(pkgParts.size() - 2), pkgParts.get(pkgParts.size() - 1));
    }

    private Optional<String> deriveComponent(final List<String> componentVersionParts) {
        // if a package starts with a period, we should ignore it because it is a virtual meta package and the version information is missing
        if (componentVersionParts == null || componentVersionParts.isEmpty() || componentVersionParts.get(0).startsWith(".")) {
            return Optional.empty();
        }
        final StringBuilder component = new StringBuilder(componentVersionParts.get(0));
        for (int i = 1; i < componentVersionParts.size() - 2; i++) {
            component.append("-");
            component.append(componentVersionParts.get(i));
        }
        return Optional.of(component.toString());
    }

    // parse output of "apk info --who-owns pkg" --> package name+version details
    private Optional<List<String>> parseIsOwnedByOutputLine(final String packageLine) {
        // expecting a line like: /usr/include/stdlib.h is owned by musl-dev-1.1.18-r3
        if (!packageLine.contains(" is owned by ")) {
            return Optional.empty();
        }
        final String[] packageLineParts = packageLine.split("\\s+");
        if (packageLineParts.length < 5) {
            return Optional.empty();
        }
        final String packageNameVersion = packageLineParts[4];
        logger.trace(String.format("packageNameAndVersion: %s", packageNameVersion));
        final String[] packageNameVersionParts = packageNameVersion.split("-");
        if (packageNameVersionParts.length < 3) {
            logger.error(String.format("apk info output contains an invalid package: %s", packageNameVersion));
            return Optional.empty();
        }
        return Optional.of(Arrays.asList(packageNameVersionParts));
    }
}
