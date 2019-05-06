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
package com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class RpmPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String NO_VALUE = "(none)";
    private final Gson gson;

    public RpmPackageManagerResolver(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public List<PackageDetails> resolvePackages(ClangPackageManagerInfo currentPackageManager, ExecutableRunner executableRunner, File workingDirectory, String queryPackageOutput) throws ExecutableRunnerException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
        final String[] packageLines = queryPackageOutput.split("\n");
        for (final String packageLine : packageLines) {
            logger.trace(String.format("packageLine: %s", packageLine));
            if (!valid(packageLine)) {
                logger.debug(String.format("Skipping line: %s", packageLine));
                continue;
            }
            final RpmPackage rpmPackage = gson.fromJson(packageLine, RpmPackage.class);
            final String packageName = rpmPackage.getName();
            String packageVersion = rpmPackage.getVersion();
            final String epoch = rpmPackage.getEpoch();
            if (!NO_VALUE.equals(epoch)) {
                packageVersion = String.format("%s:%s", epoch, packageVersion);
            }
            String arch = "";
            if (!NO_VALUE.equals(rpmPackage.getArch())) {
                arch = rpmPackage.getArch();
            }
            final PackageDetails dependencyDetails = new PackageDetails(packageName, packageVersion, arch);
            packageDetailsList.add(dependencyDetails);
        }
        return packageDetailsList;
    }

    private boolean valid(final String packageLine) {
        if (packageLine.contains(" is not owned by ")) {
            return false;
        }
        if (packageLine.contains("epoch:") && packageLine.contains("name:") && packageLine.contains("version:") && packageLine.contains("arch:")) {
            return true;
        }
        return false;
    }
}
