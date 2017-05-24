package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.packagemanager.gradle.GradleInitScriptPackager
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder

@Component
class GradlePackageManager extends PackageManager {
    @Autowired
    GradleInitScriptPackager gradleInitScriptPackager

    @Autowired
    FileFinder fileFinder

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.GRADLE
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        fileFinder.containsAllFiles(sourcePath, 'build.gradle')
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        DependencyNode rootProjectNode = gradleInitScriptPackager.extractRootProjectNode(sourcePath)
        [rootProjectNode]
    }
}