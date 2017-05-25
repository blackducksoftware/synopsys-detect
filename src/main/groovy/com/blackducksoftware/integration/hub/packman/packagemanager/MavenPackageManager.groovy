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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.MavenPackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

@Component
class MavenPackageManager extends PackageManager {
    Logger logger = LoggerFactory.getLogger(this.getClass())

    final String POM_FILENAME = 'pom.xml'

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @ValueDescription(key="packman.maven.aggregate", description="If true all maven projects will be aggregated into a single bom")
    @Value('${packman.maven.aggregate}')
    boolean aggregateBom

    @ValueDescription(key="packman.maven.scopes.included", description="The names of the dependency scopes to include")
    @Value('${packman.maven.scopes.included}')
    String includedScopes

    @ValueDescription(key="packman.maven.scopes.excluded", description="The names of the dependency scopes to exclude")
    @Value('${packman.maven.scopes.excluded}')
    String excludedScopes

    def executables = [mvn: ["mvn.cmd", "mvn"]]

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.MAVEN
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def foundExectables = fileFinder.canFindAllExecutables(executables)
        def foundFiles = fileFinder.containsAllFiles(sourcePath, POM_FILENAME)
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