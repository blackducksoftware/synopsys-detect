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
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.bomtool.nuget.NugetInspectorPackager
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.type.ExecutableType

@Component
class NugetBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(NugetBomTool.class)

    static final String SOLUTION_PATTERN = '*.sln'
    static final String PROJECT_PATTERN = '*.*proj'

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

    String nugetExecutablePath
    List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.NUGET
    }

    boolean isBomToolApplicable() {
        nugetExecutablePath = findNugetExecutable()
        packmanProperties.sourcePaths.each { sourcePath ->
            def solutionFile = fileFinder.findFile(sourcePath, SOLUTION_PATTERN)
            def projectFile = fileFinder.findFile(sourcePath, PROJECT_PATTERN)
            if (solutionFile || projectFile) {
                matchingSourcePaths.add(sourcePath)
            }
        }

        if (!matchingSourcePaths.empty && !nugetExecutablePath) {
            logger.warn('The nuget executable must be on the path - are you sure you are running on a windows system?')
        }

        nugetExecutablePath && !matchingSourcePaths.empty
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each { sourcePath ->
            DependencyNode root = nugetInspectorPackager.makeDependencyNode(sourcePath, nugetExecutablePath)
            if (!root) {
                logger.info('Unable to extract any dependencies from nuget')
            } else {
                if (isSolution(root)) {
                    root.name = projectInfoGatherer.getDefaultProjectName(BomToolType.NUGET, sourcePath, root.name)
                    root.version = projectInfoGatherer.getDefaultProjectVersionName(root.version)
                    root.externalId = new NameVersionExternalId(Forge.NUGET, root.name, root.version)
                    if (packmanProperties.nugetAggregateBom) {
                        projectNodes.add(root)
                    } else {
                        projectNodes.addAll(root.children as List)
                    }
                } else {
                    root.name = projectInfoGatherer.getDefaultProjectName(BomToolType.NUGET, sourcePath, root.name)
                    root.version = projectInfoGatherer.getDefaultProjectVersionName(root.version)
                    root.externalId = new NameVersionExternalId(Forge.NUGET, root.name, root.version)
                    projectNodes.add(root)
                }
            }
        }

        projectNodes
    }

    boolean isSolution(DependencyNode root){
        root.children != null && root.children.size() > 0 && root.children[0].children != null && root.children[0].children.size() > 0
    }

    private File findNugetExecutable() {
        if (StringUtils.isNotBlank(packmanProperties.nugetPath)) {
            new File(packmanProperties.nugetPath)
        } else {
            executableManager.getExecutable(ExecutableType.NUGET)
        }
    }
}