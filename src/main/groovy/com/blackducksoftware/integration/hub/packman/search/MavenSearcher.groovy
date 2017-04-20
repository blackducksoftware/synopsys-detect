package com.blackducksoftware.integration.hub.packman.search

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManager
import com.blackducksoftware.integration.hub.packman.parser.maven.MavenPackager

@Component
class MavenSearcher extends PackageManagerSearcher {
    public static final String POM_FILENAME = 'pom.xml'

    PackageManager getPackageManager() {
        return PackageManager.MAVEN
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        if (sourcePath && sourceDirectory.isDirectory()) {
            File pomFile = new File(sourceDirectory, POM_FILENAME)
            return pomFile.isFile()
        }

        false
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def mavenPackager = new MavenPackager(sourcePath)
        def projects = mavenPackager.makeDependencyNodes()
        return projects
    }
}