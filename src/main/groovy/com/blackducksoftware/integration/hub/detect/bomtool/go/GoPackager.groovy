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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException
import com.google.gson.Gson

@Component
class GoPackager {
    private final Logger logger = LoggerFactory.getLogger(GoPackager.class)

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    Gson gson

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    DetectConfiguration detectConfiguration

    public DependencyNode makeDependencyNodes(final String sourcePath, String goDepExecutable) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(BomToolType.GO_DEP, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(GoDepBomTool.GOLANG, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)
        GopkgLockParser gopkgLockParser = new GopkgLockParser(projectInfoGatherer)
        String goDepContents = getGopkgLockContents(new File(sourcePath), goDepExecutable)
        if(goDepContents?.trim()){
            def children = gopkgLockParser.parseDepLock(goDepContents)
            root.children.addAll(children)
        }
        return root
    }

    private String getGopkgLockContents(File file, String goDepExecutable) {
        def gopkgLockFile = new File(file, "Gopkg.lock")
        if (gopkgLockFile.exists()) {
            return gopkgLockFile.text
        }
        def gopkgLockContents = null
        try{
            logger.info("Running ${goDepExecutable} 'init' on path ${file.getAbsolutePath()}")
            Executable executable = new Executable(file, goDepExecutable, ['init'])
            executableRunner.execute(executable)
        } catch (ExecutableRunnerException e){
            logger.error("Failed to run ${goDepExecutable} 'init' on path ${file.getAbsolutePath()}, ${e.getMessage()}")
        }
        try{
            logger.info("Running ${goDepExecutable} 'ensure -update' on path ${file.getAbsolutePath()}")
            Executable executable = new Executable(file, goDepExecutable, ['ensure', '-update'])
            executableRunner.execute(executable)
        } catch (ExecutableRunnerException e){
            logger.error("Failed to run ${goDepExecutable} 'ensure -update' on path ${file.getAbsolutePath()}, ${e.getMessage()}")
        }
        if (gopkgLockFile.exists()) {
            gopkgLockContents = gopkgLockFile.text
        }
        gopkgLockContents
    }
}
