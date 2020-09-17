/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class DpkgPkgDetailsResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int PKG_INFO_LINE_LABEL_POSITION = 0;
    private static final int PKG_INFO_LINE_VALUE_POSITION = 1;

    public Optional<PackageDetails> resolvePackageDetails(ClangPackageManagerInfo currentPackageManager, ExecutableRunner executableRunner, File workingDirectory, PackageDetails pkgPartial) {
        try {
            List<String> args = new ArrayList<>(currentPackageManager.getPkgInfoArgs().get());
            args.add(pkgPartial.getPackageName());
            ExecutableOutput packageInfoOutput = executableRunner.execute(workingDirectory, currentPackageManager.getPkgMgrCmdString(), args);
            logger.debug(String.format("packageInfoOutput: %s", packageInfoOutput));
            return parsePackageDetailsFromInfoOutput(pkgPartial, packageInfoOutput.getStandardOutput());
        } catch (ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s to get package info: %s", currentPackageManager.getPkgMgrName(), e.getMessage()));
        }
        return Optional.empty();
    }

    private Optional<PackageDetails> parsePackageDetailsFromInfoOutput(PackageDetails pkg, String packageInfoOutput) {
        String[] packageInfoOutputLines = packageInfoOutput.split("\\n");
        for (String packageInfoOutputLine : packageInfoOutputLines) {
            if (foundUninstalledStatus(pkg, packageInfoOutputLine)) {
                return Optional.empty();
            }
            consumeValueFromLineIfPresent(pkg, packageInfoOutputLine, "Architecture", () -> pkg.getPackageArch(), (String a) -> pkg.setPackageArch(a));
            consumeValueFromLineIfPresent(pkg, packageInfoOutputLine, "Version", () -> pkg.getPackageVersion(), (String v) -> pkg.setPackageVersion(v));
        }
        logger.trace(String.format("Parsed package from pkg info output: %s", pkg));
        return Optional.of(pkg);
    }

    private boolean foundUninstalledStatus(PackageDetails pkg, String packageInfoOutputLine) {
        String[] packageInfoOutputLineParts = packageInfoOutputLine.split(":\\s+");
        String label = packageInfoOutputLineParts[PKG_INFO_LINE_LABEL_POSITION];
        if ("Status".equals(label.trim())) {
            if (packageInfoOutputLineParts.length > PKG_INFO_LINE_VALUE_POSITION) {
                String value = packageInfoOutputLineParts[PKG_INFO_LINE_VALUE_POSITION];
                if ((value != null) && !value.contains("installed")) {
                    logger.debug(String.format("%s is not installed; Status is: %s", pkg.getPackageName(), value));
                    return true;
                }
            } else {
                logger.warn(String.format("Missing value for Status field for package %s", pkg.getPackageName()));
            }
        }
        return false;
    }

    private boolean consumeValueFromLineIfPresent(PackageDetails pkg, String packageInfoOutputLine, String targetLabel, Supplier<String> oldValueGetter, Consumer<String> newValueSetter) {
        String[] packageInfoOutputLineParts = packageInfoOutputLine.split(":\\s+");
        String parsedLabel = packageInfoOutputLineParts[PKG_INFO_LINE_LABEL_POSITION].trim();
        if (targetLabel.equals(parsedLabel)) {
            if (packageInfoOutputLineParts.length > PKG_INFO_LINE_VALUE_POSITION) {
                String parsedValue = packageInfoOutputLineParts[PKG_INFO_LINE_VALUE_POSITION];
                String oldValue = oldValueGetter.get();
                if ((oldValue != null) && !oldValue.equals(parsedValue)) {
                    logger.warn("Package %s %s value changed from %s to %s during details resolution",
                        pkg.getPackageName(), targetLabel, oldValue, parsedValue);
                }
                newValueSetter.accept(parsedValue);
                return true;
            } else {
                logger.warn(String.format("Missing value for %s field for package %s", targetLabel, pkg.getPackageName()));
            }
        }
        return false;
    }
}
