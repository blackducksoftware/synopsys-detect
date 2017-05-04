package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.carthage.CarthagePackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder

@Component
class CarthagePackageManager extends PackageManager {
    public static final String RESOLVED_FILENAME = 'Cartfiled.resolved'

    @Autowired
    FileFinder fileFinder

    @Autowired
    CarthagePackager carthagePackager

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.CARTHAGE
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        return fileFinder.containsAllFiles(sourcePath, RESOLVED_FILENAME)
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        return carthagePackager.makeDependencyNodes(sourcePath)
    }
}