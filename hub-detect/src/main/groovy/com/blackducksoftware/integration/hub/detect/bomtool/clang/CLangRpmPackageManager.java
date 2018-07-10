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

public class CLangRpmPackageManager extends CLangLinuxPackageManager {
    private static final String PKG_MGR_NAME = "rpm";
    private static final List<String> VERSION_COMMAND_ARGS = Arrays.asList("--version");
    private static final String VERSION_OUTPUT_EXPECTED_TEXT = "RPM version";
    private static final String GET_PKG_INFO_OPTION = "-qf";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<Forge> forges = Arrays.asList(Forge.CENTOS, Forge.FEDORA, Forge.REDHAT);

    @Override
    public Forge getDefaultForge() {
        return forges.get(0);
    }

    @Override
    public List<Forge> getForges() {
        return forges;
    }

    @Override
    public List<CLangPackageDetails> getPackages(final ExecutableRunner executableRunner, final Set<File> filesForIScan, final CLangDependencyFileDetails dependencyFile) {
        final List<CLangPackageDetails> dependencyDetailsList = new ArrayList<>(3);
        try {
            final ExecutableOutput queryPackageOutput = executableRunner.executeQuietly(PKG_MGR_NAME, GET_PKG_INFO_OPTION, dependencyFile.getFile().getAbsolutePath());
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
            addToPackageList(dependencyDetailsList, queryPackageOutput.getStandardOutput());
            return dependencyDetailsList;
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s to get package details: %s", PKG_MGR_NAME, e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.info(String.format("%s should be scanned by iScan", dependencyFile.getFile().getAbsolutePath()));
                filesForIScan.add(dependencyFile.getFile());
            } else {
                logger.trace(String.format("No point in scanning %s with iScan since it's in the source.dir", dependencyFile.getFile().getAbsolutePath()));
            }
            return dependencyDetailsList;
        }
    }

    private void addToPackageList(final List<CLangPackageDetails> dependencyDetailsList, final String queryPackageOutput) {
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
            final CLangPackageDetails dependencyDetails = new CLangPackageDetails(artifact, versionRelease, arch);
            dependencyDetailsList.add(dependencyDetails);
        }
    }

    @Override
    public String getPkgMgrName() {
        return PKG_MGR_NAME;
    }

    @Override
    public List<String> getCheckPresenceCommandArgs() {
        return VERSION_COMMAND_ARGS;
    }

    @Override
    public String getCheckPresenceCommandOutputExpectedText() {
        return VERSION_OUTPUT_EXPECTED_TEXT;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private boolean valid(final String packageLine) {
        return packageLine.matches(".+-.+-.+\\..*");
    }

}
