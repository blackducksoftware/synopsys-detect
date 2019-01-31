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
package com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.clang.PackageDetails;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerInfo;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class RpmPackageManagerResolver implements ClangPackageManagerResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<PackageDetails> resolvePackages(ClangPackageManagerInfo currentPackageManager, ExecutableRunner executableRunner, File workingDirectory, String queryPackageOutput) throws ExecutableRunnerException {
        List<PackageDetails> packageDetailsList = new ArrayList<>();
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
            final PackageDetails dependencyDetails = new PackageDetails(artifact, versionRelease, arch);
            packageDetailsList.add(dependencyDetails);
        }
        return packageDetailsList;
    }

    private boolean valid(final String packageLine) {
        if (packageLine.contains(" is not owned by ")) {
            return false;
        }
        return packageLine.matches(".+-.+-.+\\..*");
    }
}
