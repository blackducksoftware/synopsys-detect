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

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

@Component
class GoBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GoBomTool.class)

    public static final Forge GOLANG = new Forge("golang",":")

    @Autowired
    GoPackager goPackager

    List<String> matchingSourcePaths = []

    boolean needToInstallGoDep = false

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO;
    }

    @Override
    public boolean isBomToolApplicable() {
        def goExecutablePath = findGoExecutable()
        if (!goExecutablePath?.trim()) {
            logger.debug('Could not find Go on the environment PATH')
        }
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePatternWithDepth('*.go')
        goExecutablePath && !matchingSourcePaths.isEmpty()
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        def godepExecutable = findGoDepExecutable()
        if (!godepExecutable?.trim()) {
            def goExecutable = findGoExecutable()
            godepExecutable = installGoDep(goExecutable)
        }
        def nodes = []
        matchingSourcePaths.each {
            nodes.addAll(goPackager.makeDependencyNodes(it, godepExecutable))
        }
        return nodes
    }

    private String findGoExecutable() {
        executableManager.getPathOfExecutable(ExecutableType.GO)
    }

    private String findGoDepExecutable() {
        String godepPath = detectProperties.godepPath
        if (StringUtils.isBlank(godepPath)) {
            godepPath = executableManager.getPathOfExecutable(ExecutableType.GODEP)
        }
        godepPath
    }

    private String installGoDep(String goExecutable){
        def outputDirectory = new File(detectProperties.outputDirectoryPath)
        def goOutputDirectory = new File(outputDirectory, 'Go')
        logger.debug("Installing godep in ${goOutputDirectory}")
        Executable getGoDep = new Executable(goOutputDirectory, goExecutable, [
            'get',
            'github.com/tools/godep'
        ])
        executableRunner.executeLoudly(getGoDep)

        Executable buildGoDep = new Executable(goOutputDirectory, goExecutable, [
            'build',
            'github.com/tools/godep'
        ])
        executableRunner.executeLoudly(buildGoDep)
        (new File(goOutputDirectory, 'godep')).getAbsolutePath()
    }
}