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
package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetails;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.PackageDetails;

public class ClangPackageManagerRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean applies(ClangPackageManager currentPackageManager, File workingDirectory, final ExecutableRunner executor) {
        ClangPackageManagerInfo packageManagerInfo = currentPackageManager.getPackageManagerInfo();
        try {
            final ExecutableOutput versionOutput = executor.execute(workingDirectory, packageManagerInfo.getPkgMgrName(), packageManagerInfo.getCheckPresenceCommandArgs());
            logger.debug(String.format("packageStatusOutput: %s", versionOutput.getStandardOutput()));
            if (versionOutput.getStandardOutput().contains(packageManagerInfo.getCheckPresenceCommandOutputExpectedText())) {
                logger.info(String.format("Found package manager %s", packageManagerInfo.getPkgMgrName()));
                return true;
            }
            logger.debug(String.format("Output of %s %s does not look right; concluding that the %s package manager is not present. The output: %s", packageManagerInfo.getPkgMgrName(), packageManagerInfo.getCheckPresenceCommandArgs(), packageManagerInfo.getPkgMgrName(), versionOutput));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error executing %s %s; concluding that the %s package manager is not present. The error: %s", packageManagerInfo.getPkgMgrName(), packageManagerInfo.getCheckPresenceCommandArgs(), packageManagerInfo.getPkgMgrName(), e.getMessage()));
            return false;
        }
        return false;
    }

    public List<PackageDetails> getPackages(ClangPackageManager currentPackageManager, File workingDirectory, final ExecutableRunner executableRunner, final Set<File> unManagedDependencyFiles, final DependencyFileDetails dependencyFile) {
        ClangPackageManagerInfo packageManagerInfo = currentPackageManager.getPackageManagerInfo();
        final List<PackageDetails> dependencyDetailsList = new ArrayList<>(3);
        try {
            final List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
            fileSpecificGetOwnerArgs.add(dependencyFile.getFile().getAbsolutePath());
            final ExecutableOutput queryPackageOutput = executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs);
            logger.debug(String.format("queryPackageOutput: %s", queryPackageOutput));
            this.addToPackageList(currentPackageManager, executableRunner, workingDirectory, dependencyDetailsList, queryPackageOutput.getStandardOutput());
            return dependencyDetailsList;
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s: %s", packageManagerInfo.getPkgMgrCmdString(), e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.debug(String.format("%s is not managed by %s", dependencyFile.getFile().getAbsolutePath(), packageManagerInfo.getPkgMgrCmdString()));
                unManagedDependencyFiles.add(dependencyFile.getFile());
            } else {
                logger.debug(String.format("%s is not managed by %s, but it's in the source.dir", dependencyFile.getFile().getAbsolutePath(), packageManagerInfo.getPkgMgrCmdString()));
            }
            return dependencyDetailsList;
        }
    }

    protected void addToPackageList(ClangPackageManager currentPackageManager, final ExecutableRunner executableRunner, File workingDirectory, final List<PackageDetails> dependencyDetailsList, final String queryPackageOutput) throws ExecutableRunnerException {
        List<PackageDetails> parsed = currentPackageManager.getPackageResolver().resolvePackages(currentPackageManager.getPackageManagerInfo(), executableRunner, workingDirectory, queryPackageOutput);
        dependencyDetailsList.addAll(parsed);
    }

}
