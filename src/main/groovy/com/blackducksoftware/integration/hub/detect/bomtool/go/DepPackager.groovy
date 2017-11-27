/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.go

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DepPackager {
    private final Logger logger = LoggerFactory.getLogger(DepPackager.class)

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    Gson gson

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExternalIdFactory externalIdFactory

    public DependencyGraph makeDependencyGraph(final String sourcePath, String goDepExecutable) {
        GopkgLockParser gopkgLockParser = new GopkgLockParser(externalIdFactory)
        String goDepContents = getGopkgLockContents(new File(sourcePath), goDepExecutable)
        if (goDepContents?.trim()) {
            return gopkgLockParser.parseDepLock(goDepContents)
        }
        return null
    }

    private String getGopkgLockContents(File file, String goDepExecutable) {
        def gopkgLockFile = new File(file, "Gopkg.lock")
        if (gopkgLockFile.exists()) {
            return gopkgLockFile.text
        }

        //by default, we won't run 'init' and 'ensure' anymore so just return an empty string
        if (!detectConfiguration.goRunDepInit) {
            return ''
        }

        def gopkgTomlFile = new File(file, "Gopkg.toml")
        def vendorDirectory = new File(file, "vendor")
        boolean vendorDirectoryExistedBefore = vendorDirectory.exists()
        def vendorDirectoryBackup = new File(file, "vendor_old")
        if (vendorDirectoryExistedBefore) {
            logger.info("Backing up ${vendorDirectory.getAbsolutePath()} to ${vendorDirectoryBackup.getAbsolutePath()}")
            FileUtils.moveDirectory(vendorDirectory, vendorDirectoryBackup)
        }

        def gopkgLockContents = null
        try {
            logger.info("Running ${goDepExecutable} 'init' on path ${file.getAbsolutePath()}")
            Executable executable = new Executable(file, goDepExecutable, ['init'])
            executableRunner.execute(executable)
        } catch (ExecutableRunnerException e) {
            logger.error("Failed to run ${goDepExecutable} 'init' on path ${file.getAbsolutePath()}, ${e.getMessage()}")
        }
        try {
            logger.info("Running ${goDepExecutable} 'ensure -update' on path ${file.getAbsolutePath()}")
            Executable executable = new Executable(file, goDepExecutable, ['ensure', '-update'])
            executableRunner.execute(executable)
        } catch (ExecutableRunnerException e) {
            logger.error("Failed to run ${goDepExecutable} 'ensure -update' on path ${file.getAbsolutePath()}, ${e.getMessage()}")
        }
        if (gopkgLockFile.exists()) {
            gopkgLockContents = gopkgLockFile.text
            gopkgLockFile.delete()
            gopkgTomlFile.delete()
            FileUtils.deleteDirectory(vendorDirectory)
            if (vendorDirectoryExistedBefore) {
                logger.info("Restoring back up ${vendorDirectory.getAbsolutePath()} from ${vendorDirectoryBackup.getAbsolutePath()}")
                FileUtils.moveDirectory(vendorDirectoryBackup, vendorDirectory)
            }
        }
        gopkgLockContents
    }
}
