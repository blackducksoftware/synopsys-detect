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

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.MavenPackager
import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableManager

@Component
class MavenPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String POM_FILENAME = 'pom.xml'

    @Autowired
    MavenPackager mavenPackager

    @Autowired
    ExecutableManager executableManager

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @ValueDescription(description="The path of the Maven executable")
    @Value('${packman.maven.path}')
    String mavenPath

    def executables = [mvn: ["mvn.cmd", "mvn"]]

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.MAVEN
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def mvnExecutable = findMavenExecutablePath()
        def pomXml = fileFinder.findFile(sourcePath, POM_FILENAME)

        mvnExecutable && pomXml
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def projects = mavenPackager.makeDependencyNodes(sourcePath, findMavenExecutablePath())
        return projects
    }

    private String findMavenExecutablePath() {
        if (StringUtils.isBlank(mavenPath)) {
            return executableManager.getPathOfExecutable(ExecutableType.MVN)
        }
        mavenPath
    }
}