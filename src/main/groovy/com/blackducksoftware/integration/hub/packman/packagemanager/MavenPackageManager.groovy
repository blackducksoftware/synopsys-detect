package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.MavenPackager
import com.blackducksoftware.integration.hub.packman.util.ExecutableFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

@Component
class MavenPackageManager extends PackageManager {
    public static final String POM_FILENAME = 'pom.xml'

    @Autowired
    ExecutableFinder executableFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Value('${packman.maven.aggregate}')
    boolean aggregateBom

    @Value('${packman.maven.scopes.included}')
    String includedScopes

    @Value('${packman.maven.scopes.excluded}')
    String excludedScopes

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.MAVEN
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
        File sourceDirectory = new File(sourcePath)
        ExcludedIncludedFilter excludedIncludedFilter = new ExcludedIncludedFilter(excludedScopes.toLowerCase(), includedScopes.toLowerCase())
        def mavenPackager = new MavenPackager(excludedIncludedFilter, projectInfoGatherer, executableFinder, sourceDirectory, aggregateBom)
        def projects = mavenPackager.makeDependencyNodes()
        return projects
    }
}