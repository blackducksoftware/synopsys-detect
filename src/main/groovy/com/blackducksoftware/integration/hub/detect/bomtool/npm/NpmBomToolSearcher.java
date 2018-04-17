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
package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.search.BaseBomToolSearcher;
import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolSearchResultFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnBomToolSearcher;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class NpmBomToolSearcher extends BaseBomToolSearcher<NpmBomToolSearchResult> {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";
    private final Logger logger = LoggerFactory.getLogger(NpmBomToolSearcher.class);
    @Autowired
    private YarnBomToolSearcher yarnBomToolSearcher;

    @Override
    public NpmBomToolSearchResult getSearchResult(final File directoryToSearch) {
        if (yarnBomToolSearcher.getSearchResult(directoryToSearch).isApplicable()) {
            logger.debug("The npm bomtool does not apply because yarn applies.");
            return BomToolSearchResultFactory.createNpmDoesNotApply();
        }

        final File packageLockJson = getDetectFileManager().findFile(directoryToSearch, NpmBomToolSearcher.PACKAGE_LOCK_JSON);
        final File shrinkwrapJson = getDetectFileManager().findFile(directoryToSearch, NpmBomToolSearcher.SHRINKWRAP_JSON);

        final boolean containsNodeModules = getDetectFileManager().containsAllFiles(directoryToSearch, NpmBomToolSearcher.NODE_MODULES);
        final boolean containsPackageJson = getDetectFileManager().containsAllFiles(directoryToSearch, NpmBomToolSearcher.PACKAGE_JSON);
        final boolean containsPackageLockJson = packageLockJson != null && packageLockJson.exists();
        final boolean containsShrinkwrapJson = shrinkwrapJson != null && shrinkwrapJson.exists();

        String npmExePath = null;
        String npmVersion = null;
        if (containsPackageJson && containsNodeModules) {
            npmExePath = findExecutablePath(ExecutableType.NPM, true, directoryToSearch, getDetectConfiguration().getNpmPath());
            npmVersion = getNpmVersion(directoryToSearch, npmExePath);
        }
        logRelevantMessages(directoryToSearch, containsNodeModules, containsPackageJson, containsPackageLockJson, containsShrinkwrapJson, npmExePath, npmVersion);
        if (StringUtils.isBlank(npmVersion)) {
            npmExePath = null;
        }

        final boolean lockFileIsApplicable = containsShrinkwrapJson || containsPackageLockJson;
        final boolean isApplicable = lockFileIsApplicable || (containsNodeModules && StringUtils.isNotBlank(npmExePath));

        if (isApplicable) {
            return BomToolSearchResultFactory.createNpmApplies(directoryToSearch, npmExePath, packageLockJson, shrinkwrapJson);
        } else {
            return BomToolSearchResultFactory.createNpmDoesNotApply();
        }
    }

    private void logRelevantMessages(final File directoryToSearch, final boolean containsNodeModules, final boolean containsPackageJson, final boolean containsPackageLockJson, final boolean containsShrinkwrapJson, final String npmExePath,
            final String npmVersion) {
        if (containsPackageJson && !containsNodeModules && !containsPackageLockJson && !containsShrinkwrapJson) {
            logger.warn(String.format("package.json was located in %s, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.", directoryToSearch.getAbsolutePath()));
        } else if (containsPackageJson && containsNodeModules) {
            if (StringUtils.isBlank(npmExePath)) {
                logger.warn(String.format("Could not find an %s executable", getExecutableManager().getExecutableName(ExecutableType.NPM)));
            } else if (StringUtils.isNotBlank(npmVersion)) {
                logger.debug(String.format("Npm version %s", npmVersion));
            }
        } else if (containsPackageLockJson) {
            logger.info(String.format("Using %s", NpmBomToolSearcher.PACKAGE_LOCK_JSON));
        } else if (containsShrinkwrapJson) {
            logger.info(String.format("Using %s", NpmBomToolSearcher.SHRINKWRAP_JSON));
        }
    }

    private String getNpmVersion(final File directoryToSearch, final String npmExePath) {
        if (StringUtils.isNotBlank(npmExePath)) {
            Executable npmVersionExe = null;
            final List<String> arguments = new ArrayList<>();
            arguments.add("-version");

            String npmNodePath = getDetectConfiguration().getNpmNodePath();
            if (StringUtils.isNotBlank(npmNodePath)) {
                final int lastSlashIndex = npmNodePath.lastIndexOf("/");
                if (lastSlashIndex >= 0) {
                    npmNodePath = npmNodePath.substring(0, lastSlashIndex);
                }
                final Map<String, String> environmentVariables = new HashMap<>();
                environmentVariables.put("PATH", npmNodePath);

                npmVersionExe = new Executable(directoryToSearch, environmentVariables, npmExePath, arguments);
            } else {
                npmVersionExe = new Executable(directoryToSearch, npmExePath, arguments);
            }
            try {
                return getExecutableRunner().execute(npmVersionExe).getStandardOutput();
            } catch (final ExecutableRunnerException e) {
                logger.error(String.format("Could not run npm to get the version: %s", e.getMessage()));
            }
        }
        return null;
    }

}
