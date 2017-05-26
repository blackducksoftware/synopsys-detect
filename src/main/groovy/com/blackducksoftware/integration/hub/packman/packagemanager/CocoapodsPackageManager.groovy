package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.CocoapodsPackager
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder

@Component
class CocoapodsPackageManager extends PackageManager {
    @Autowired
    FileFinder fileFinder

    @Autowired
    CocoapodsPackager cocoapodsPackager2

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.COCOAPODS
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        return fileFinder.containsAllFiles(sourcePath, 'Podfile.lock')
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        return cocoapodsPackager2.makeDependencyNodes(sourcePath)
    }
}