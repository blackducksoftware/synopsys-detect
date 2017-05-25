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

import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.packagemanager.nuget.NugetInspectorPackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.commands.Executable

@Component
class NugetPackageManager extends PackageManager {
    Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @ValueDescription(key="packman.nuget.aggregate", description="If true all nuget projects will be aggregated into a single bom")
    @Value('${packman.nuget.aggregate}')
    boolean aggregateBom

    def executables = [nuget: ["NuGet.exe"]]

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.NUGET
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        boolean containsFiles = fileFinder.containsAllFiles(sourcePath, '*.sln')
        boolean foundExectables = fileFinder.canFindAllExecutables(executables)
        boolean OSCompatable = SystemUtils.IS_OS_WINDOWS
        if(containsFiles && !OSCompatable) {
            logger.info('Can not execute HubNugetInspector on a non-windows system')
        }
        containsFiles && OSCompatable && foundExectables
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        Executable nuget = fileFinder.findExecutables(executables)['nuget']
        DependencyNode solution = nugetInspectorPackager.makeDependencyNode(sourcePath, nuget)
        if(!solution) {
            logger.info('Unable to extract any dependencies from nuget')
            return []
        }

        solution.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.NUGET, sourcePath, solution.name)
        solution.version = projectInfoGatherer.getDefaultProjectVersionName(solution.version)
        solution.externalId = new NameVersionExternalId(Forge.nuget, solution.name, solution.version)
        if(aggregateBom) {
            return [solution]
        }
        solution.children as List
    }
}