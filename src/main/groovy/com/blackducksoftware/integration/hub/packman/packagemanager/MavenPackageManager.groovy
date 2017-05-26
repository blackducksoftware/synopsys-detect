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
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.MavenPackager
import com.blackducksoftware.integration.hub.packman.type.CommandType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.command.CommandManager

@Component
class MavenPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String POM_FILENAME = 'pom.xml'

    @Autowired
    MavenPackager mavenPackager

    @Autowired
    CommandManager commandManager

    @Autowired
    FileFinder fileFinder

    @Value('${packman.maven.path}')
    String mavenPath

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.MAVEN
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def mvnCommand = findMavenCommandPath()
        def pomXml = fileFinder.findFile(sourcePath, POM_FILENAME)

        mvnCommand && pomXml
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def projects = mavenPackager.makeDependencyNodes(findMavenCommandPath(), sourcePath)
        return projects
    }

    private String findMavenCommandPath() {
        if (StringUtils.isBlank(mavenPath)) {
            return commandManager.getPathOfCommand(CommandType.MVN)
        }
        mavenPath
    }
}