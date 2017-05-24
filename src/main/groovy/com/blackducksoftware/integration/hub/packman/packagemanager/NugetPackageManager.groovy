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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
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

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

    @Autowired
    CommandManager commandManager

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Value('${packman.nuget.aggregate}')
    boolean aggregateBom

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.NUGET
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def nugetCommand = commandManager.getCommand(CommandType.NUGET)
        def solutionFile = fileFinder.findFile(sourcePath, SOLUTION_PATTERN)

        if (solutionFile && !nugetCommand) {
            logger.info('Can not execute nuget on a non-windows system')
        }
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def nugetCommand = commandManager.getCommand(CommandType.NUGET)
        DependencyNode solution = nugetInspectorPackager.makeDependencyNode(sourcePath, nugetCommand)
        if (!solution) {
            logger.info('Unable to extract any dependencies from nuget')
            return []
        }

        solution.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.NUGET, sourcePath, solution.name)
        solution.version = projectInfoGatherer.getDefaultProjectVersionName(solution.version)
        solution.externalId = new NameVersionExternalId(Forge.nuget, solution.name, solution.version)
        if (aggregateBom) {
            return [solution]
        }
        solution.children as List
    }
}