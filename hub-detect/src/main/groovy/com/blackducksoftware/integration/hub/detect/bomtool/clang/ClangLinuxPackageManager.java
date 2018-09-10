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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.synopsys.integration.hub.bdio.model.Forge;

public abstract class ClangLinuxPackageManager {
    private final String pkgMgrName;
    private final String pkgMgrCmdString;
    private final List<Forge> forges;
    private final Logger logger;
    private final List<String> checkPresenceCommandArgs;
    private final String checkPresenceCommandOutputExpectedText;
    private final List<String> pkgMgrGetOwnerCmdArgs;

    public ClangLinuxPackageManager(final Logger logger, final String pkgMgrName, final String pkgMgrCmdString, final List<Forge> forges, final List<String> checkPresenceCommandArgs, final String checkPresenceCommandOutputExpectedText,
            final List<String> pkgMgrGetOwnerCmdArgs) {
        this.logger = logger;
        this.pkgMgrName = pkgMgrName;
        this.pkgMgrCmdString = pkgMgrCmdString;
        this.forges = forges;
        this.checkPresenceCommandArgs = checkPresenceCommandArgs;
        this.checkPresenceCommandOutputExpectedText = checkPresenceCommandOutputExpectedText;
        this.pkgMgrGetOwnerCmdArgs = pkgMgrGetOwnerCmdArgs;
    }

    public boolean applies(File workingDirectory, final ExecutableRunner executor) {
        try {
            final ExecutableOutput versionOutput = executor.execute(workingDirectory, getPkgMgrName(), getCheckPresenceCommandArgs());
            logger.debug(String.format("packageStatusOutput: %s", versionOutput.getStandardOutput()));
            if (versionOutput.getStandardOutput().contains(getCheckPresenceCommandOutputExpectedText())) {
                logger.info(String.format("Found package manager %s", getPkgMgrName()));
                return true;
            }
            logger.debug(String.format("Output of %s %s does not look right; concluding that the %s package manager is not present. The output: %s", getPkgMgrName(), getCheckPresenceCommandArgs(), getPkgMgrName(), versionOutput));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error executing %s %s; concluding that the %s package manager is not present. The error: %s", getPkgMgrName(), getCheckPresenceCommandArgs(), getPkgMgrName(), e.getMessage()));
            return false;
        }
        return false;
    }

    public List<PackageDetails> getPackages(File workingDirectory, final ExecutableRunner executableRunner, final Set<File> unManagedDependencyFiles, final DependencyFileDetails dependencyFile) {
        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        try {
            final List<String> fileSpecificGetOwnerArgs = new ArrayList<>(pkgMgrGetOwnerCmdArgs);
            fileSpecificGetOwnerArgs.add(dependencyFile.getFile().getAbsolutePath());
            final ExecutableOutput queryPackageOutput = executableRunner.executeQuietly(workingDirectory, pkgMgrCmdString, fileSpecificGetOwnerArgs);
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
            this.addToPackageList(executableRunner, workingDirectory, dependencyDetailsList, queryPackageOutput.getStandardOutput());
            return dependencyDetailsList;
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s: %s", pkgMgrCmdString, e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.debug(String.format("%s is not managed by %s", dependencyFile.getFile().getAbsolutePath(), pkgMgrCmdString));
                unManagedDependencyFiles.add(dependencyFile.getFile());
            } else {
                logger.debug(String.format("%s is not managed by %s, but it's in the source.dir", dependencyFile.getFile().getAbsolutePath(), pkgMgrCmdString));
            }
            return dependencyDetailsList;
        }
    }

    public abstract Forge getDefaultForge();

    protected abstract void addToPackageList(final ExecutableRunner executableRunner, File workingDirectory, final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) throws ExecutableRunnerException;

    public String getPkgMgrName() {
        return pkgMgrName;
    }

    public List<Forge> getForges() {
        return forges;
    }

    public List<String> getCheckPresenceCommandArgs() {
        return checkPresenceCommandArgs;
    }

    public String getCheckPresenceCommandOutputExpectedText() {
        return checkPresenceCommandOutputExpectedText;
    }
}
