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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class RpmPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String NO_VALUE = "(none)";
    private final Gson gson;

    public RpmPackageManagerResolver(Gson gson) {
        this.gson = gson;
    }

    @Override
    public List<PackageDetails> resolvePackages(ClangPackageManagerInfo currentPackageManager, DetectableExecutableRunner executableRunner, File workingDirectory, String queryPackageOutput)
        throws ExecutableRunnerException, NotOwnedByAnyPkgException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        String[] packageLines = queryPackageOutput.split("\n");
        for (String packageLine : packageLines) {
            Optional<PackageDetails> dependencyDetails = generatePackageFromQueryOutputLine(packageLine);
            if (dependencyDetails.isPresent()) {
                packageDetailsList.add(dependencyDetails.get());
            }
        }
        return packageDetailsList;
    }

    public Optional<PackageDetails> generatePackageFromQueryOutputLine(String queryOutputLine) throws NotOwnedByAnyPkgException {
        logger.trace(String.format("packageLine: %s", queryOutputLine));
        Optional<String> packageJson = extractPackageJson(queryOutputLine);
        if (!packageJson.isPresent()) {
            logger.debug(String.format("Skipping line: %s (not a package)", queryOutputLine));
            return Optional.empty();
        }
        RpmPackage rpmPackage;
        try {
            rpmPackage = gson.fromJson(packageJson.get(), RpmPackage.class);
        } catch (JsonSyntaxException e) {
            logger.warn(String.format("Skipping rpm 'who owns this file' query output line: %s (invalid JSON syntax)", queryOutputLine));
            return Optional.empty();
        }
        PackageDetails dependencyDetails = buildPackageDetails(rpmPackage);
        return Optional.of(dependencyDetails);
    }

    private Optional<String> extractPackageJson(String queryOutputLine) throws NotOwnedByAnyPkgException {
        queryOutputLine = queryOutputLine.trim();
        if (queryOutputLine.contains(" is not owned by ")) {
            // The file queried is not owned by any package known to pkg mgr
            throw new NotOwnedByAnyPkgException(queryOutputLine);
        }
        if (queryOutputLine.contains("epoch:") && queryOutputLine.contains("name:") && queryOutputLine.contains("version:") && queryOutputLine.contains("arch:")) {
            int indexOfSecondPkgVariant = queryOutputLine.indexOf('{', 1);
            if (indexOfSecondPkgVariant < 0) {
                // This line contains a single package variant; it's fine as-is
                return Optional.of(queryOutputLine);
            } else {
                // This line contains multiple package variants; return first variant
                return Optional.of(queryOutputLine.substring(0, indexOfSecondPkgVariant));
            }
        } else {
            // This line contains no package
            return Optional.empty();
        }
    }

    @NotNull
    private PackageDetails buildPackageDetails(RpmPackage rpmPackage) {
        String packageName = rpmPackage.getName();
        String packageVersion = rpmPackage.getVersion();
        String epoch = rpmPackage.getEpoch();
        if (!NO_VALUE.equals(epoch)) {
            packageVersion = String.format("%s:%s", epoch, packageVersion);
        }
        String arch = "";
        if (!NO_VALUE.equals(rpmPackage.getArch())) {
            arch = rpmPackage.getArch();
        }
        PackageDetails dependencyDetails = new PackageDetails(packageName, packageVersion, arch);
        return dependencyDetails;
    }
}
