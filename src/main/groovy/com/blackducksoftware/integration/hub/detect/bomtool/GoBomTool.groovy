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
        String godepPath = packmanProperties.getGodepPath()
        if (StringUtils.isBlank(godepPath)) {
            godepPath = executableManager.getPathOfExecutable(ExecutableType.GODEP)
        }
        godepPath
    }

    private String installGoDep(String goExecutable){
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
        (new File(outputDirectory, 'godep')).getAbsolutePath()
    }
}