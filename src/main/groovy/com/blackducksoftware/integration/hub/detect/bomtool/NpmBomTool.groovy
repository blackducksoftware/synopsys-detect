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
package com.blackducksoftware.integration.hub.detect.bomtool

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfilePackager
import com.blackducksoftware.integration.hub.detect.bomtool.search.NpmBomToolSearchResult
import com.blackducksoftware.integration.hub.detect.bomtool.search.NpmBomToolSearcher
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@TypeChecked
class NpmBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(NpmBomTool.class);

    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    @Autowired
    private NpmCliDependencyFinder npmCliDependencyFinder;

    @Autowired
    private NpmLockfilePackager npmLockfilePackager;

    @Autowired
    private YarnBomTool yarnBomTool;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private NpmBomToolSearcher npmBomToolSearcher;

    private String npmExePath;
    private File packageLockJson;
    private File shrinkwrapJson;

    @Override
    public BomToolType getBomToolType() {
        BomToolType.NPM
    }

    @Override
    public boolean isBomToolApplicable() {
        NpmBomToolSearchResult searchResult = npmBomToolSearcher.getBomToolSearchResult(sourcePath);
        if (searchResult.isApplicable()) {
            npmExePath = searchResult.getNpmExePath();
            packageLockJson = searchResult.getPackageLockJson();
            shrinkwrapJson = searchResult.getShrinkwrapJson();
            return true;
        }

        return false;
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        List<DetectCodeLocation> codeLocations = []
        if (npmExePath) {
            codeLocations.addAll(extractFromCommand())
        } else if (packageLockJson) {
            codeLocations.addAll(extractFromLockFile(packageLockJson))
        } else if (shrinkwrapJson) {
            codeLocations.addAll(extractFromLockFile(shrinkwrapJson))
        }

        if (!codeLocations.empty) {
            hubSignatureScanner.registerPathToScan(ScanPathSource.NPM_SOURCE, sourceDirectory, NpmBomToolSearcher.NODE_MODULES)
        }

        codeLocations
    }

    private List<DetectCodeLocation> extractFromLockFile(File lockFile) {
        String lockFileText = lockFile.getText()
        DetectCodeLocation detectCodeLocation = npmLockfilePackager.parse(sourcePath, lockFileText, detectConfiguration.npmIncludeDevDependencies)

        [detectCodeLocation]
    }

    private List<DetectCodeLocation> extractFromCommand() {
        File npmLsOutputFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.OUTPUT_FILE)
        File npmLsErrorFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.ERROR_FILE)

        boolean includeDevDeps = detectConfiguration.npmIncludeDevDependencies
        def exeArgs = ['ls', '-json']
        if (!includeDevDeps) {
            exeArgs.add('-prod')
        }
        Executable npmLsExe = new Executable(new File(sourcePath), npmExePath, exeArgs)
        executableRunner.executeToFile(npmLsExe, npmLsOutputFile, npmLsErrorFile)

        if (npmLsOutputFile.length() > 0) {
            if (npmLsErrorFile.length() > 0) {
                logger.debug("Error when running npm ls -json command")
                logger.debug(npmLsErrorFile.text)
            }
            def detectCodeLocation = npmCliDependencyFinder.generateCodeLocation(sourcePath, npmLsOutputFile)

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
