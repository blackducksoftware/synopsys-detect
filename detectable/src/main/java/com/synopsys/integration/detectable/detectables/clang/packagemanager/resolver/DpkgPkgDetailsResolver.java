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

    public Optional<PackageDetails> resolvePackageDetails(ClangPackageManagerInfo currentPackageManager, ExecutableRunner executableRunner, File workingDirectory, PackageDetails pkg) {
        try {
            List<String> args = new ArrayList<>(currentPackageManager.getPkgInfoArgs().get());
            args.add(pkg.getPackageName());
            ExecutableOutput packageStatusOutput = executableRunner.execute(workingDirectory, currentPackageManager.getPkgMgrCmdString(), args);
            logger.debug(String.format("packageStatusOutput: %s", packageStatusOutput));
            return parsePackageDetailsFromStatusOutput(pkg, packageStatusOutput.getStandardOutput());
        } catch (ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s to get package info: %s", currentPackageManager.getPkgMgrName(), e.getMessage()));
        }
        return Optional.empty();
    }

    private Optional<PackageDetails> parsePackageDetailsFromStatusOutput(PackageDetails pkg, String packageStatusOutput) {
        String[] packageStatusOutputLines = packageStatusOutput.split("\\n");
        for (String packageStatusOutputLine : packageStatusOutputLines) {
            if (foundUninstalledStatus(pkg, packageStatusOutputLine)) {
                return Optional.empty();
            }
            consumeValueFromLineIfPresent(pkg, packageStatusOutputLine, "Architecture", () -> pkg.getPackageArch(), (String a) -> pkg.setPackageArch(a));
            consumeValueFromLineIfPresent(pkg, packageStatusOutputLine, "Version", () -> pkg.getPackageVersion(), (String v) -> pkg.setPackageVersion(v));
        }
        return Optional.of(pkg);
    }

    private boolean foundUninstalledStatus(PackageDetails pkg, String packageStatusOutputLine) {
        String[] packageStatusOutputLineNameValue = packageStatusOutputLine.split(":\\s+");
        String label = packageStatusOutputLineNameValue[0];
        if ("Status".equals(label.trim())) {
            if (packageStatusOutputLineNameValue.length > 1) {
                String value = packageStatusOutputLineNameValue[1];
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

    private boolean consumeValueFromLineIfPresent(PackageDetails pkg, String packageStatusOutputLine, String targetLabel, Supplier<String> oldValueGetter, Consumer<String> newValueConsumer) {
        String[] packageStatusOutputLineNameValue = packageStatusOutputLine.split(":\\s+");
        String parsedLabel = packageStatusOutputLineNameValue[0].trim();
        if (targetLabel.equals(parsedLabel)) {
            if (packageStatusOutputLineNameValue.length > 1) {
                String parsedValue = packageStatusOutputLineNameValue[1];
                String oldValue = oldValueGetter.get();
                if ((oldValue != null) && !oldValue.equals(parsedValue)) {
                    logger.warn("Package %s %s value changed from %s to %s during details resolution",
                        pkg.getPackageName(), targetLabel, oldValue, parsedValue);
                }
                newValueConsumer.accept(parsedValue);
                return true;
            } else {
                logger.warn(String.format("Missing value for %s field for package %s", targetLabel, pkg.getPackageName()));
            }
        }
        return false;
    }
}
