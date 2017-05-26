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
package com.blackducksoftware.integration.hub.packman.packagemanager

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.packagemanager.nuget.NugetInspectorPackager
import com.blackducksoftware.integration.hub.packman.type.CommandType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.command.CommandManager

@Component
class NugetPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(NugetPackageManager.class)

    static final String SOLUTION_PATTERN = '*.sln'

    static final String PROJECT_PATTERN = '*.*proj'

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

    @Autowired
    CommandManager commandManager

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @ValueDescription(key="packman.nuget.aggregate", description="If true all nuget projects will be aggregated into a single bom")
    @Value('${packman.nuget.aggregate}')
    boolean aggregateBom

    @ValueDescription(key="packman.nuget.path", description="The path of the Nuget executable")
    @Value('${packman.nuget.path}')
    String nugetPath

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.NUGET
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def nugetCommand = findNugetCommand()
        def solutionFile = fileFinder.findFile(sourcePath, SOLUTION_PATTERN)
        def projectFile = fileFinder.findFile(sourcePath, PROJECT_PATTERN)

        if (projectFile && solutionFile && !nugetCommand) {
            logger.info('Can not execute nuget on a non-windows system')
        }

        nugetCommand && (solutionFile || projectFile)
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def nugetCommand = findNugetCommand()
        DependencyNode root = nugetInspectorPackager.makeDependencyNode(sourcePath, nugetCommand)
        if (!root) {
            logger.info('Unable to extract any dependencies from nuget')
            return []
        }
        if(isSolution(root)){
            root.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.NUGET, sourcePath, root.name)
            root.version = projectInfoGatherer.getDefaultProjectVersionName(root.version)
            root.externalId = new NameVersionExternalId(Forge.nuget, root.name, root.version)
            if (aggregateBom) {
                return [root]
            }
            root.children as List
        } else{
            root.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.NUGET, sourcePath, root.name)
            root.version = projectInfoGatherer.getDefaultProjectVersionName(root.version)
            root.externalId = new NameVersionExternalId(Forge.nuget, root.name, root.version)
            [root]
        }
    }

    boolean isSolution(DependencyNode root){
        root.children != null && root.children.size() > 0 && root.children[0].children != null && root.children[0].children.size() > 0
    }

    private File findNugetCommand() {
        if (StringUtils.isNotBlank(nugetPath)) {
            new File(nugetPath)
        } else {
            commandManager.getCommand(CommandType.NUGET)
        }
    }
}