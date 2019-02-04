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
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;

public class DpkgPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DpkgVersionResolver versionResolver;
    public DpkgPackageManagerResolver(DpkgVersionResolver versionResolver){
        this.versionResolver = versionResolver;
    }
    @Override
    public List<PackageDetails> resolvePackages(ClangPackageManagerInfo currentPackageManager, ExecutableRunner executableRunner, File workingDirectory, String queryPackageOutput) throws ExecutableRunnerException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        final String[] packageLines = queryPackageOutput.split("\n");
        for (final String packageLine : packageLines) {
            if (!valid(packageLine)) {
                logger.debug(String.format("Skipping line: %s", packageLine));
                continue;
            }
            final String[] queryPackageOutputParts = packageLine.split("\\s+");
            final String[] packageNameArchParts = queryPackageOutputParts[0].split(":");
            final String packageName = packageNameArchParts[0];
            final String packageArch = packageNameArchParts[1];
            logger.debug(String.format("package name: %s; arch: %s", packageName, packageArch));
            final Optional<String> packageVersion = versionResolver.resolvePackageVersion(currentPackageManager, executableRunner, workingDirectory, packageName);
            final PackageDetails dependencyDetails = new PackageDetails(packageName, packageVersion.orElse(null), packageArch);
            packageDetailsList.add(dependencyDetails);
        }
        return packageDetailsList;
    }

    private boolean valid(final String packageLine) {
        return packageLine.matches(".+:.+: .+");
    }
}
