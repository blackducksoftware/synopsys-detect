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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class NpmBomTool extends BomTool {
    def final static NODE_MODULES = 'node_modules'
    def final static OUTPUT_FILE = 'detect_npm_proj_dependencies.json'

    @Autowired
    NpmCliDependencyFinder cliDependencyFinder

    private List<String> npmPaths = []
    private String npmExe

    @Override
    public BomToolType getBomToolType() {
        BomToolType.NPM
    }

    @Override
    public boolean isBomToolApplicable() {
        npmPaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern(NODE_MODULES)
        npmExe = getExecutablePath()

        npmPaths && npmExe
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> nodes = []

        npmPaths.each {
            nodes.add(cliDependencyFinder.generateDependencyNode(it, npmExe))
        }

        nodes
    }

    private String getExecutablePath() {
        if (!detectConfiguration.getNpmPath()) {
            return executableManager.getPathOfExecutable(ExecutableType.NPM)
        }
        detectConfiguration.getNpmPath()
    }
}
