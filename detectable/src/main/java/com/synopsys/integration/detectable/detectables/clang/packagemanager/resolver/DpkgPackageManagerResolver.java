/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class DpkgPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DpkgPkgDetailsResolver versionResolver;

    public DpkgPackageManagerResolver(DpkgPkgDetailsResolver versionResolver) {
        this.versionResolver = versionResolver;
    }

    @Override
    public List<PackageDetails> resolvePackages(ClangPackageManagerInfo currentPackageManager, DetectableExecutableRunner executableRunner, File workingDirectory, String ownershipQueryOutput)
        throws NotOwnedByAnyPkgException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        String[] packageLines = ownershipQueryOutput.split("\n");
        for (String packageLine : packageLines) {
            if (!valid(packageLine)) {
                logger.trace(String.format("Skipping file ownership query output line: %s", packageLine));
                continue;
            }
            String[] queryPackageOutputParts = packageLine.split("\\s+");
            String[] packageNameArchParts = queryPackageOutputParts[0].split(":");
            String packageName = packageNameArchParts[0].trim();
            logger.debug(String.format("File ownership query results: package name: %s", packageName));
            Optional<PackageDetails> pkg = versionResolver.resolvePackageDetails(currentPackageManager, executableRunner, workingDirectory, packageName);
            if (pkg.isPresent()) {
                logger.debug(String.format("Adding package: %s", pkg.get()));
                packageDetailsList.add(pkg.get());
            }
        }
        return packageDetailsList;
    }

    private boolean valid(String packageLine) throws NotOwnedByAnyPkgException {
        if (packageLine.contains("no path found matching pattern")) {
            throw new NotOwnedByAnyPkgException(packageLine);
        }
        // arch included
        if (packageLine.matches(".+:.+: .+")) {
            return true;
        }
        // arch not included
        if (packageLine.matches(".+: .+")) {
            return true;
        }
        return false;
    }
}
