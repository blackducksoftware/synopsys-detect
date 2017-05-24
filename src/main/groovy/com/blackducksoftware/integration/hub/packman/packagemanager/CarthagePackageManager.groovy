package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.packagemanager.carthage.CarthagePackager
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder

@Component
class CarthagePackageManager extends PackageManager {
    @Autowired
    FileFinder fileFinder

    @Autowired
    CarthagePackager carthagePackager

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.CARTHAGE
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        return fileFinder.containsAllFiles(sourcePath, 'Cartfiled.resolved')
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        return carthagePackager.makeDependencyNodes(sourcePath)
    }
}