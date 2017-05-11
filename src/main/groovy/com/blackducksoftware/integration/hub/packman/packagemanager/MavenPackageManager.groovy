package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.MavenPackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

@Component
class MavenPackageManager extends PackageManager {
    public static final String POM_FILENAME = 'pom.xml'

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Value('${packman.maven.aggregate}')
    boolean aggregateBom

    @Value('${packman.maven.scopes.included}')
    String includedScopes

    @Value('${packman.maven.scopes.excluded}')
    String excludedScopes

    def executables = [mvn: ["mvn.cmd", "mvn"]]

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.MAVEN
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def foundExectables = fileFinder.findExecutables(executables)
        def foundFiles = fileFinder.findFile(sourcePath, POM_FILENAME)
        return foundExectables && foundFiles
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        ExcludedIncludedFilter excludedIncludedFilter = new ExcludedIncludedFilter(excludedScopes.toLowerCase(), includedScopes.toLowerCase())
        def mavenPackager = new MavenPackager(excludedIncludedFilter, projectInfoGatherer, sourceDirectory, aggregateBom, fileFinder.findExecutables(executables))
        def projects = mavenPackager.makeDependencyNodes()
        return projects
    }
}