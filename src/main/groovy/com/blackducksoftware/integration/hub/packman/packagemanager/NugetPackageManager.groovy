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
import com.blackducksoftware.integration.hub.packman.packagemanager.nuget.NugetInspectorPackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

@Component
class NugetPackageManager extends PackageManager {
    Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

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
        boolean containsFiles = fileFinder.containsAllFiles(sourcePath, 'packages.config')
        boolean OSCompatable = SystemUtils.IS_OS_WINDOWS
        if(containsFiles && !OSCompatable) {
            logger.info('Could not execute Nuget Inspector on a non-windows system')
        }
        containsFiles && OSCompatable
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        DependencyNode solution = nugetInspectorPackager.makeDependencyNode(sourcePath)
        solution.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.NUGET, sourcePath, solution.name)
        solution.version = projectInfoGatherer.getDefaultProjectVersionName(solution.version)
        solution.externalId = new NameVersionExternalId(Forge.nuget, solution.name, solution.version)
        if(aggregateBom) {
            return [solution]
        }
        solution.children as List
    }
}