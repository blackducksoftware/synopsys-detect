/*
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
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class DpkgVersionResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<String> resolvePackageVersion(ClangPackageManagerInfo currentPackageManager, DetectableExecutableRunner executableRunner, File workingDirectory, String packageName) {
        try {
            List<String> args = new ArrayList<>(currentPackageManager.getPkgInfoArgs().get());
            args.add(packageName);
            ExecutableOutput packageStatusOutput = executableRunner.execute(workingDirectory, currentPackageManager.getPkgMgrCmdString(), args);
            logger.debug(String.format("packageStatusOutput: %s", packageStatusOutput));
            return parsePackageVersionFromStatusOutput(packageName, packageStatusOutput.getStandardOutput());
        } catch (ExecutableRunnerException e) {
            logger.error(String.format("Error executing %s to get package info: %s", currentPackageManager.getPkgMgrName(), e.getMessage()));
        }
        return Optional.empty();
    }

    private Optional<String> parsePackageVersionFromStatusOutput(String packageName, String packageStatusOutput) {
        String[] packageStatusOutputLines = packageStatusOutput.split("\\n");
        for (String packageStatusOutputLine : packageStatusOutputLines) {
            String[] packageStatusOutputLineNameValue = packageStatusOutputLine.split(":\\s+");
            String label = packageStatusOutputLineNameValue[0];
            String value = packageStatusOutputLineNameValue[1];
            if ("Status".equals(label.trim()) && !value.contains("installed")) {
                logger.debug(String.format("%s is not installed; Status is: %s", packageName, value));
                return Optional.empty();
            }
            if ("Version".equals(label)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
