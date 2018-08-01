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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public class RpmPackageManager extends ClangLinuxPackageManager {
    private static final String PKG_MGR_NAME = "rpm";
    private static final List<String> VERSION_COMMAND_ARGS = Arrays.asList("--version");
    private static final String VERSION_OUTPUT_EXPECTED_TEXT = "RPM version";
    private static final String GET_PKG_INFO_OPTION = "-qf";

    private static final Logger logger = LoggerFactory.getLogger(RpmPackageManager.class);

    public RpmPackageManager() {
        super(logger, PKG_MGR_NAME, PKG_MGR_NAME, Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT), VERSION_COMMAND_ARGS,
                VERSION_OUTPUT_EXPECTED_TEXT, Arrays.asList(GET_PKG_INFO_OPTION));
    }

    @Override
    protected void addToPackageList(final ExecutableRunner executableRunner, final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) {
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

    @Override
    public Forge getDefaultForge() {
        return Forge.CENTOS;
    }

    private boolean valid(final String packageLine) {
        return packageLine.matches(".+-.+-.+\\..*");
    }

}
