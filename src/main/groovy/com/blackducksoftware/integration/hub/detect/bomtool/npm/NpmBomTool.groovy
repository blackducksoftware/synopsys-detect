/*
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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolSearchOptions
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnBomTool
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NpmBomTool extends BomTool<NpmApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(NpmBomTool.class);

    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    @Autowired
    private NpmCliDependencyFinder npmCliDependencyFinder;

    @Autowired
    private NpmLockfilePackager npmLockfilePackager;

    @Autowired
    private YarnBomTool yarnBomTool;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Override
    public BomToolType getBomToolType() {
        BomToolType.NPM
    }

    BomToolSearchOptions getSearchOptions() {
        return new BomToolSearchOptions(false, Integer.MAX_VALUE);
    }

    //TODO: Bom tool finder - npm does not apply if YARN does.
    @Override
    public NpmApplicableResult isBomToolApplicable(final File directory) {
        String npmExePath = null;
        final File packageLockJson = detectFileManager.findFile(directory, PACKAGE_LOCK_JSON);
        final File shrinkwrapJson = detectFileManager.findFile(directory, SHRINKWRAP_JSON);

        final boolean containsNodeModules = detectFileManager.containsAllFiles(directory, NODE_MODULES);
        final boolean containsPackageJson = detectFileManager.containsAllFiles(directory, PACKAGE_JSON);
        final boolean containsPackageLockJson = packageLockJson != null && packageLockJson.exists();
        final boolean containsShrinkwrapJson = shrinkwrapJson != null && shrinkwrapJson.exists();

        if (containsPackageJson && !containsNodeModules) {
            logger.warn(String.format("package.json was located in %s, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.", directory.getAbsolutePath()));
        } else if (containsPackageJson && containsNodeModules) {
            npmExePath = executableManager.getExecutablePathOrOverride(ExecutableType.NPM, true, directory, detectConfiguration.getNpmPath());
            if (StringUtils.isBlank(npmExePath)) {
                logger.warn(String.format("Could not find an %s executable", executableManager.getExecutableName(ExecutableType.NPM)));
            } else {
                Executable npmVersionExe = null;
                final List<String> arguments = new ArrayList<>();
                arguments.add("-version");

                String npmNodePath = detectConfiguration.getNpmNodePath();
                if (StringUtils.isNotBlank(npmNodePath)) {
                    final int lastSlashIndex = npmNodePath.lastIndexOf("/");
                    if (lastSlashIndex >= 0) {
                        npmNodePath = npmNodePath.substring(0, lastSlashIndex);
                    }
                    final Map<String, String> environmentVariables = new HashMap<>();
                    environmentVariables.put("PATH", npmNodePath);

                    npmVersionExe = new Executable(directory, environmentVariables, npmExePath, arguments);
                } else {
                    npmVersionExe = new Executable(directory, npmExePath, arguments);
                }
                try {
                    final String npmVersion = executableRunner.execute(npmVersionExe).getStandardOutput();
                    logger.debug(String.format("Npm version %s", npmVersion));
                } catch (final ExecutableRunnerException e) {
                    logger.error(String.format("Could not run npm to get the version: %s", e.getMessage()));
                    return null;
                }
            }
        } else if (containsPackageLockJson) {
            logger.info(String.format("Using %s", PACKAGE_LOCK_JSON));
        } else if (containsShrinkwrapJson) {
            logger.info(String.format("Using %s", SHRINKWRAP_JSON));
        }

        final boolean lockFileIsApplicable = containsShrinkwrapJson || containsPackageLockJson;
        final boolean isApplicable = lockFileIsApplicable || (containsNodeModules && StringUtils.isNotBlank(npmExePath));

        if (isApplicable) {
            return new NpmApplicableResult(directory, npmExePath, packageLockJson, shrinkwrapJson);
        } else {
            return null;
        }
    }

    public BomToolExtractionResult extractDetectCodeLocations(NpmApplicableResult applicable) {
        List<DetectCodeLocation> codeLocations = []
        if (applicable.npmExePath) {
            codeLocations.addAll(extractFromCommand(applicable))
        } else if (applicable.packageLockJson) {
            codeLocations.addAll(extractFromLockFile(applicable.packageLockJson, applicable.directory))
        } else if (applicable.shrinkwrapJson) {
            codeLocations.addAll(extractFromLockFile(applicable.shrinkwrapJson, applicable.directory))
        }

        if (!codeLocations.empty) {
            hubSignatureScanner.registerPathToScan(ScanPathSource.NPM_SOURCE, applicable.directory, NODE_MODULES)
        }

        bomToolExtractionResultsFactory.fromCodeLocations(codeLocations, getBomToolType(), applicable.directory)
    }

    private List<DetectCodeLocation> extractFromLockFile(File lockFile, File directory) {
        String lockFileText = lockFile.getText()
        DetectCodeLocation detectCodeLocation = npmLockfilePackager.parse(directory.canonicalPath, lockFileText, detectConfiguration.npmIncludeDevDependencies)

        [detectCodeLocation]
    }

    private List<DetectCodeLocation> extractFromCommand(NpmApplicableResult applicable) {
        File npmLsOutputFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.OUTPUT_FILE)
        File npmLsErrorFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.ERROR_FILE)

        boolean includeDevDeps = detectConfiguration.npmIncludeDevDependencies
        def exeArgs = ['ls', '-json']
        if (!includeDevDeps) {
            exeArgs.add('-prod')
        }
        Executable npmLsExe = new Executable(applicable.directory, applicable.npmExePath, exeArgs)
        executableRunner.executeToFile(npmLsExe, npmLsOutputFile, npmLsErrorFile)

        if (npmLsOutputFile.length() > 0) {
            if (npmLsErrorFile.length() > 0) {
                logger.debug("Error when running npm ls -json command")
                logger.debug(npmLsErrorFile.text)
            }
            def detectCodeLocation = npmCliDependencyFinder.generateCodeLocation(applicable.directory.canonicalPath, npmLsOutputFile)

            return [detectCodeLocation]
        } else if (npmLsErrorFile.length() > 0) {
            logger.error("Error when running npm ls -json command")
            logger.debug(npmLsErrorFile.text)
        } else {
            logger.warn("Nothing returned from npm ls -json command")
        }

        []
    }
}
