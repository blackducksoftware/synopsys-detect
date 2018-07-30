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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class RpmPackageManager extends LinuxPackageManager {
    private static final String PKG_MGR_NAME = "rpm";
    private static final List<String> VERSION_COMMAND_ARGS = Arrays.asList("--version");
    private static final String VERSION_OUTPUT_EXPECTED_TEXT = "RPM version";
    private static final String GET_PKG_INFO_OPTION = "-qf";

    private static final Logger logger = LoggerFactory.getLogger(RpmPackageManager.class);

    public RpmPackageManager() {
        super(logger, PKG_MGR_NAME, Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT), VERSION_COMMAND_ARGS,
                VERSION_OUTPUT_EXPECTED_TEXT);
    }

    @Override
    public List<PackageDetails> getPackages(final ExecutableRunner executableRunner, final Set<File> unManagedDependencyFiles, final DependencyFileDetails dependencyFile) {
        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        try {
            final ExecutableOutput queryPackageOutput = executableRunner.executeQuietly(PKG_MGR_NAME, GET_PKG_INFO_OPTION, dependencyFile.getFile().getAbsolutePath());
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
            addToPackageList(dependencyDetailsList, queryPackageOutput.getStandardOutput());
            return dependencyDetailsList;
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s to get package details: %s", PKG_MGR_NAME, e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.debug(String.format("%s is not managed by %s", dependencyFile.getFile().getAbsolutePath(), PKG_MGR_NAME));
                unManagedDependencyFiles.add(dependencyFile.getFile());
            } else {
                logger.debug(String.format("%s is not managed by %s, but it's in the source.dir", dependencyFile.getFile().getAbsolutePath(), PKG_MGR_NAME));
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
            final int lastDotIndex = packageLine.lastIndexOf('.');
            final String arch = packageLine.substring(lastDotIndex + 1);
            final int lastDashIndex = packageLine.lastIndexOf('-');
            final String nameVersion = packageLine.substring(0, lastDashIndex);
            final int secondToLastDashIndex = nameVersion.lastIndexOf('-');
            final String versionRelease = packageLine.substring(secondToLastDashIndex + 1, lastDotIndex);
            final String artifact = packageLine.substring(0, secondToLastDashIndex);
            final PackageDetails dependencyDetails = new PackageDetails(artifact, versionRelease, arch);
            dependencyDetailsList.add(dependencyDetails);
        }
    }

    private boolean valid(final String packageLine) {
        return packageLine.matches(".+-.+-.+\\..*");
    }

}
