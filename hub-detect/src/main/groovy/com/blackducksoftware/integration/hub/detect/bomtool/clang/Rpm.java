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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class Rpm implements PkgMgr {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String PKG_MGR_NAME = "rpm";
    private static final String VERSION_COMMAND = "rpm --version";
    private static final String EXPECTED_TEXT = "RPM version";
    private static final String QUERY_DEPENDENCY_FILE_COMMAND_PATTERN = "rpm -qf %s";

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
    public List<PackageDetails> getDependencyDetails(final CommandStringExecutor executor, final Set<File> filesForIScan, final DependencyFile dependencyFile) {
        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        final String getPackageCommand = String.format(QUERY_DEPENDENCY_FILE_COMMAND_PATTERN, dependencyFile.getFile().getAbsolutePath());
        try {
            final String queryPackageOutput = executor.execute(new File("."), null, getPackageCommand);
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
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
                final PackageDetails dependencyDetails = new PackageDetails(Optional.ofNullable(artifact), Optional.ofNullable(versionRelease), Optional.ofNullable(arch));
                dependencyDetailsList.add(dependencyDetails);
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
    public String getPkgMgrName() {
        return PKG_MGR_NAME;
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
        return packageLine.matches(".+-.+-.+\\..*");
    }

}
