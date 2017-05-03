package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.CocoapodsPackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.PackageManagerFile
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

@Component
class CocoapodsPackageManager extends PackageManager {
    def PODFILE = new PackageManagerFile('Podfile.lock', true)

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    FileFinder fileFinder

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.COCOAPODS
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        return fileFinder.containsFiles(sourcePath, PODFILE);
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def cocoaPodsPackager = new CocoapodsPackager(projectInfoGatherer, fileFinder.findFile(sourcePath,PODFILE), sourcePath)
        return cocoaPodsPackager.makeDependencyNodes()
    }
}