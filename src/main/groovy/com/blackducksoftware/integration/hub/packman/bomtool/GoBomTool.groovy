/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.bomtool

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.packman.bomtool.go.GoPackager
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
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
        def goExecutablePath = findGoDepExecutable()
        if (!goExecutablePath?.trim()) {
            needToInstallGoDep = true
            goExecutablePath = findGoExecutable()
        }
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePatternWithDepth('*.go')
        def b = goExecutablePath && !matchingSourcePaths.isEmpty()
        return b
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        def godepExecutable = null
        if (needToInstallGoDep) {
            godepExecutable = installGoDep()
        } else {
            godepExecutable = findGoDepExecutable()
        }
        def nodes = []
        matchingSourcePaths.each {
            nodes.addAll(goPackager.makeDependencyNodes(it, godepExecutable))
        }
        return nodes
    }

    private String findGoExecutable() {
        String goPath = packmanProperties.getGoPath()
        if (StringUtils.isBlank(goPath)) {
            goPath = executableManager.getPathOfExecutable(ExecutableType.GO)
        }
        goPath
    }

    private String findGoDepExecutable() {
        String godepPath = packmanProperties.getGodepPath()
        if (StringUtils.isBlank(godepPath)) {
            godepPath = executableManager.getPathOfExecutable(ExecutableType.GODEP)
        }

        godepPath
    }

    private String installGoDep(){
        def goExecutable = findGoExecutable()
        def outputDirectory = new File(packmanProperties.getOutputDirectoryPath())
        logger.debug("Installing godep in ${outputDirectory}")
        Executable getGoDep = new Executable(outputDirectory, goExecutable, [
            'get',
            'github.com/tools/godep'
        ])
        executableRunner.executeLoudly(getGoDep)

        Executable buildGoDep = new Executable(outputDirectory, goExecutable, [
            'build',
            'github.com/tools/godep'
        ])
        executableRunner.executeLoudly(buildGoDep)
    }
}