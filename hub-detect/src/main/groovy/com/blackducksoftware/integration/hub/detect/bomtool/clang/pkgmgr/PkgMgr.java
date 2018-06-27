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
package com.blackducksoftware.integration.hub.detect.bomtool.clang.pkgmgr;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.DependencyFile;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.PackageDetails;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.executor.CommandStringExecutor;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public interface PkgMgr {
    // TODO Should not use CommandStringExecutor here; each pkg mgr could easily build the list of args
    default boolean applies(final CommandStringExecutor executor) {
        try {
            final String versionOutput = executor.execute(new File("."), new HashMap<String, String>(), getCheckPresenceCommand());
            getLogger().debug(String.format("packageStatusOutput: %s", versionOutput));
            if (versionOutput.contains(getCheckPresenceCommandOutputExpectedText())) {
                getLogger().info(String.format("Found package manager %s", getPkgMgrName()));
                return true;
            }
            getLogger().debug(String.format("Output of %s does not look right; concluding that the dpkg package manager is not present. The output: %s", getCheckPresenceCommand(), versionOutput));
        } catch (ExecutableRunnerException | IntegrationException e) {
            getLogger().debug(String.format("Error executing %s; concluding that the dpkg package manager is not present. The error: %s", getCheckPresenceCommand(), e.getMessage()));
            return false;
        }
        return false;
    }

    String getPkgMgrName();

    Forge getDefaultForge();

    List<Forge> getForges();

    // TODO Should not use CommandStringExecutor here; each pkg mgr could easily build the list of args
    List<PackageDetails> getDependencyDetails(CommandStringExecutor executor, Set<File> filesForIScan, DependencyFile dependencyFile);

    String getCheckPresenceCommand();

    String getCheckPresenceCommandOutputExpectedText();

    Logger getLogger();
}
