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
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class Apk extends PkgMgr {
    private static final String PKG_MGR_NAME = "apk";
    private static final String VERSION_COMMAND = "apk --version";
    private static final String EXPECTED_TEXT = "apk-tools ";
    private static final String QUERY_ARCH_COMMAND = "apk info --print-arch";
    private static final String QUERY_DEPENDENCY_FILE_COMMAND_PATTERN = "apk info --who-owns %s";

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
    public List<PackageDetails> getDependencyDetails(final CommandStringExecutor executor, final Set<File> filesForIScan, final DependencyFile dependencyFile) {
        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        final String getPackageCommand = String.format(QUERY_DEPENDENCY_FILE_COMMAND_PATTERN, dependencyFile.getFile().getAbsolutePath());
        try {
            if (architecture == null) {
                architecture = executor.execute(new File("."), null, QUERY_ARCH_COMMAND).trim();
                logger.debug(String.format("architecture: %s", architecture));
            }
            final String queryPackageOutput = executor.execute(new File("."), null, getPackageCommand);
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
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
                String component = "";
                for (int i = 0; i < parts.length - 2; i++) {
                    final String part = parts[i];
                    if (StringUtils.isNotBlank(component)) {
                        component += String.format("-%s", part);
                    } else {
                        component = part;
                    }
                }
                logger.trace(String.format("component: %s", component));
                // if a package starts with a period, we should ignore it because it is a virtual meta package and the version information is missing
                if (!component.startsWith(".")) {
                    final String externalId = String.format("%s/%s/%s", component, version, architecture);
                    logger.debug(String.format("Constructed externalId: %s", externalId));
                    final PackageDetails dependencyDetails = new PackageDetails(Optional.ofNullable(component), Optional.ofNullable(version), Optional.ofNullable(architecture));
                    dependencyDetailsList.add(dependencyDetails);
                }
            }
            return dependencyDetailsList;
        } catch (ExecutableRunnerException | IntegrationException e) {
            logger.error(String.format("Error executing %s: %s", getPackageCommand, e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.info(String.format("%s should be scanned by iScan", dependencyFile.getFile().getAbsolutePath()));
                filesForIScan.add(dependencyFile.getFile());
            } else {
                logger.trace(String.format("No point in scanning %s with iScan since it's in the source.dir", dependencyFile.getFile().getAbsolutePath()));
            }
            return dependencyDetailsList;
        }
    }

    @Override
    public String getCheckPresenceCommand() {
        return VERSION_COMMAND;
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
