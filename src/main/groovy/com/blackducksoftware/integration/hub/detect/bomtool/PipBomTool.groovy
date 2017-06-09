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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipPackager
import com.blackducksoftware.integration.hub.detect.bomtool.pip.VirtualEnvironment
import com.blackducksoftware.integration.hub.detect.bomtool.pip.VirtualEnvironmentHandler
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class PipBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String SETUP_FILENAME = 'setup.py'

    @Autowired
    PipPackager pipPackager

    @Autowired
    VirtualEnvironmentHandler virtualEnvironmentHandler

    List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        if(detectProperties.pipThreeOverride)
            BomToolType.PIP3
        else
            BomToolType.PIP
    }

    boolean isBomToolApplicable() {
        VirtualEnvironment systemEnvironment = virtualEnvironmentHandler.getSystemEnvironment()
        def foundExectables = systemEnvironment.pipPath && systemEnvironment.pythonPath
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern(SETUP_FILENAME)
        def definedRequirements = detectProperties.requirementsFilePath
        foundExectables && (!matchingSourcePaths.empty || definedRequirements)
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each { sourcePath ->
            def sourceDirectory = new File(sourcePath)
            VirtualEnvironment virtualEnv = virtualEnvironmentHandler.getVirtualEnvironment(sourceDirectory)
            projectNodes.addAll(pipPackager.makeDependencyNodes(sourcePath, virtualEnv))
        }

        projectNodes
    }
}