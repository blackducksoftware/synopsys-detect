/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.clang.DependencyFileDetails;
import com.blackducksoftware.integration.hub.detect.detector.clang.PackageDetails;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.synopsys.integration.bdio.model.Forge;

public class ClangLinuxPackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean applies(ClangPackageManagerInfo currentPackageManager, File workingDirectory, final ExecutableRunner executor) {
        try {
            final ExecutableOutput versionOutput = executor.execute(workingDirectory, currentPackageManager.getPkgMgrName(), currentPackageManager.getCheckPresenceCommandArgs());
            logger.debug(String.format("packageStatusOutput: %s", versionOutput.getStandardOutput()));
            if (versionOutput.getStandardOutput().contains(currentPackageManager.getCheckPresenceCommandOutputExpectedText())) {
                logger.info(String.format("Found package manager %s", currentPackageManager.getPkgMgrName()));
                return true;
            }
            logger.debug(String.format("Output of %s %s does not look right; concluding that the %s package manager is not present. The output: %s", currentPackageManager.getPkgMgrName(), currentPackageManager.getCheckPresenceCommandArgs(), currentPackageManager.getPkgMgrName(), versionOutput));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error executing %s %s; concluding that the %s package manager is not present. The error: %s", currentPackageManager.getPkgMgrName(), currentPackageManager.getCheckPresenceCommandArgs(), currentPackageManager.getPkgMgrName(), e.getMessage()));
            return false;
        }
        return false;
    }

    public List<PackageDetails> getPackages(ClangPackageManagerInfo currentPackageManager, File workingDirectory, final ExecutableRunner executableRunner, final Set<File> unManagedDependencyFiles, final DependencyFileDetails dependencyFile) {

        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        try {
            final List<String> fileSpecificGetOwnerArgs = new ArrayList<>(currentPackageManager.getPkgMgrGetOwnerCmdArgs());
            fileSpecificGetOwnerArgs.add(dependencyFile.getFile().getAbsolutePath());
            final ExecutableOutput queryPackageOutput = executableRunner.executeQuietly(workingDirectory, currentPackageManager.getPkgMgrCmdString(), fileSpecificGetOwnerArgs);
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
            this.addToPackageList(currentPackageManager, executableRunner, workingDirectory, dependencyDetailsList, queryPackageOutput.getStandardOutput());
            return dependencyDetailsList;
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s: %s", currentPackageManager.getPkgMgrCmdString(), e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.debug(String.format("%s is not managed by %s", dependencyFile.getFile().getAbsolutePath(), currentPackageManager.getPkgMgrCmdString()));
                unManagedDependencyFiles.add(dependencyFile.getFile());
            } else {
                logger.debug(String.format("%s is not managed by %s, but it's in the source.dir", dependencyFile.getFile().getAbsolutePath(), currentPackageManager.getPkgMgrCmdString()));
            }
            return dependencyDetailsList;
        }
    }

    protected void addToPackageList(ClangPackageManagerInfo currentPackageManager, final ExecutableRunner executableRunner, File workingDirectory, final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) throws ExecutableRunnerException {
        List<PackageDetails> parsed = currentPackageManager.getPackageOutputParser().resolvePackages(currentPackageManager,executableRunner, workingDirectory, queryPackageOutput);
        dependencyDetailsList.addAll(parsed);
    }

}
