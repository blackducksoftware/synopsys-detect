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

    public boolean applies(final ExecutableRunner executor) {
        try {
            final ExecutableOutput versionOutput = executor.execute(getPkgMgrName(), getCheckPresenceCommandArgs());
            getLogger().debug(String.format("packageStatusOutput: %s", versionOutput.getStandardOutput()));
            if (versionOutput.getStandardOutput().contains(getCheckPresenceCommandOutputExpectedText())) {
                getLogger().info(String.format("Found package manager %s", getPkgMgrName()));
                return true;
            }
            getLogger().debug(String.format("Output of %s %s does not look right; concluding that the dpkg package manager is not present. The output: %s", getPkgMgrName(), getCheckPresenceCommandArgs(), versionOutput));
        } catch (final ExecutableRunnerException e) {
            getLogger().debug(String.format("Error executing %s %s; concluding that the dpkg package manager is not present. The error: %s", getPkgMgrName(), getCheckPresenceCommandArgs(), e.getMessage()));
            return false;
        }
        return false;
    }

    public abstract String getPkgMgrName();

    public abstract Forge getDefaultForge();

    public abstract List<Forge> getForges();

    public abstract List<PackageDetails> getPackages(ExecutableRunner executableRunner, Set<File> filesForIScan, DependencyFileDetails dependencyFile);

    public abstract List<String> getCheckPresenceCommandArgs();

    public abstract String getCheckPresenceCommandOutputExpectedText();

    public abstract Logger getLogger();
}
