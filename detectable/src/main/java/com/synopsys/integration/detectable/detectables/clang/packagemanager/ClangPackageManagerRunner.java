/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;

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
            logger.debug(String.format("Output of %s %s does not look right; concluding that the %s package manager is not present. The output: %s", packageManagerInfo.getPkgMgrName(), packageManagerInfo.getCheckPresenceCommandArgs(),
                packageManagerInfo.getPkgMgrName(), versionOutput));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error executing %s %s; concluding that the %s package manager is not present. The error: %s", packageManagerInfo.getPkgMgrName(), packageManagerInfo.getCheckPresenceCommandArgs(),
                packageManagerInfo.getPkgMgrName(), e.getMessage()));
            return false;
        }
        return false;
    }

    public PackageDetailsResult getAllPackages(ClangPackageManager currentPackageManager, File workingDirectory, final ExecutableRunner executableRunner, final Set<DependencyFileDetails> dependencyFiles) {
        Set<PackageDetails> packageDetails = new HashSet<>();
        Set<File> unmanagedDependencies = new HashSet<>();
        for (DependencyFileDetails dependencyFile : dependencyFiles) {
            PackageDetailsResult packageDetailsResult = getPackages(currentPackageManager, workingDirectory, executableRunner, dependencyFile);
            packageDetails.addAll(packageDetailsResult.getFoundPackages());
            unmanagedDependencies.addAll(packageDetailsResult.getUnmanagedDependencies());
        }

        return new PackageDetailsResult(packageDetails, unmanagedDependencies);
    }

    public PackageDetailsResult getPackages(ClangPackageManager currentPackageManager, File workingDirectory, final ExecutableRunner executableRunner, final DependencyFileDetails dependencyFile) {
        ClangPackageManagerInfo packageManagerInfo = currentPackageManager.getPackageManagerInfo();
        final Set<PackageDetails> dependencyDetails = new HashSet<>();
        final Set<File> unManagedDependencyFiles = new HashSet<>();
        try {
            final List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
            fileSpecificGetOwnerArgs.add(dependencyFile.getFile().getAbsolutePath());
            final ExecutableOutput queryPackageOutput = executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs);

            ClangPackageManagerResolver resolver = currentPackageManager.getPackageResolver();
            List<PackageDetails> packageDetails = resolver.resolvePackages(currentPackageManager.getPackageManagerInfo(), executableRunner, workingDirectory, queryPackageOutput.getStandardOutput());
            dependencyDetails.addAll(packageDetails);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s: %s", packageManagerInfo.getPkgMgrCmdString(), e.getMessage()));
            if (!dependencyFile.isInBuildDir()) {
                logger.debug(String.format("%s is not managed by %s", dependencyFile.getFile().getAbsolutePath(), packageManagerInfo.getPkgMgrCmdString()));
                unManagedDependencyFiles.add(dependencyFile.getFile());
            } else {
                logger.debug(String.format("%s is not managed by %s, but it's in the source.dir", dependencyFile.getFile().getAbsolutePath(), packageManagerInfo.getPkgMgrCmdString()));
            }
        }
        return new PackageDetailsResult(dependencyDetails, unManagedDependencyFiles);
    }

}
