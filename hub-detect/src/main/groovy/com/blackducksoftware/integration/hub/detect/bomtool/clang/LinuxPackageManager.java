/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public abstract class LinuxPackageManager {
    private final String pkgMgrName;
    private final List<Forge> forges;
    private final Logger logger;
    private final List<String> checkPresenceCommandArgs;
    private final String checkPresenceCommandOutputExpectedText;

    public LinuxPackageManager(final Logger logger, final String pkgMgrName, final List<Forge> forges, final List<String> checkPresenceCommandArgs, final String checkPresenceCommandOutputExpectedText) {
        this.logger = logger;
        this.pkgMgrName = pkgMgrName;
        this.forges = forges;
        this.checkPresenceCommandArgs = checkPresenceCommandArgs;
        this.checkPresenceCommandOutputExpectedText = checkPresenceCommandOutputExpectedText;
    }

    public abstract List<PackageDetails> getPackages(ExecutableRunner executableRunner, Set<File> unManagedDependencyFiles, DependencyFileDetails dependencyFile);

    public boolean applies(final ExecutableRunner executor) {
        try {
            final ExecutableOutput versionOutput = executor.execute(getPkgMgrName(), getCheckPresenceCommandArgs());
            logger.debug(String.format("packageStatusOutput: %s", versionOutput.getStandardOutput()));
            if (versionOutput.getStandardOutput().contains(getCheckPresenceCommandOutputExpectedText())) {
                logger.info(String.format("Found package manager %s", getPkgMgrName()));
                return true;
            }
            logger.debug(String.format("Output of %s %s does not look right; concluding that the dpkg package manager is not present. The output: %s", getPkgMgrName(), getCheckPresenceCommandArgs(), versionOutput));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error executing %s %s; concluding that the dpkg package manager is not present. The error: %s", getPkgMgrName(), getCheckPresenceCommandArgs(), e.getMessage()));
            return false;
        }
        return false;
    }

    public String getPkgMgrName() {
        return pkgMgrName;
    }

    public Forge getDefaultForge() {
        return forges.get(0);
    }

    public List<Forge> getForges() {
        return forges;
    }

    public List<String> getCheckPresenceCommandArgs() {
        return checkPresenceCommandArgs;
    }

    public String getCheckPresenceCommandOutputExpectedText() {
        return checkPresenceCommandOutputExpectedText;
    }
}
