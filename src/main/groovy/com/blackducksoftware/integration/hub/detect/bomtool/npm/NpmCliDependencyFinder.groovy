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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.bomtool.NpmBomTool
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

@Component
class NpmCliDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(NpmCliDependencyFinder.class)
    private String rootPath

    @Autowired
    Gson gson

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    NameVersionNodeTransformer nodeTransformer

    public DependencyNode generateDependencyNode(String rootDirectoryPath, String exePath) {
        DependencyNode result;
        rootPath = rootDirectoryPath

        def npmLsExe = new Executable(new File(rootDirectoryPath), exePath, ['ls', '-json'])
        def exeRunner = new ExecutableRunner()
        def tempJsonOutFile = new File(NpmBomTool.OUTPUT_FILE)

        exeRunner.executeToFile(npmLsExe, tempJsonOutFile)

        if(tempJsonOutFile?.length() > 0) {
            logger.info("Running npm ls and converting values")
            result = convertNpmJsonFileToDependencyNode(tempJsonOutFile)
        }
        if(tempJsonOutFile) {
            try {
                tempJsonOutFile.delete()
            } catch (IOException e) {
                println(e.stackTrace)
            }
        }

        if(!result) {
            //Check for any package-lock or shrinkwrap files in future
        }

        result
    }

    private DependencyNode convertNpmJsonFileToDependencyNode(File depOut) {
        NpmCliNode node = gson.fromJson(new JsonReader(new FileReader(depOut)), NpmCliNode.class)

        if(rootPath) {
            node.name = node.name ?: projectInfoGatherer.getDefaultProjectName(BomToolType.NPM, rootPath)
        }
        node.version = node.version ?: projectInfoGatherer.getDefaultProjectVersionName()

        nodeTransformer.createDependencyNode(Forge.NPM, node)
    }
}
