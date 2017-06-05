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
package com.blackducksoftware.integration.hub.packman.bomtool

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.bomtool.maven.MavenPackager
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.SourcePathSearcher
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableManager

@Component
class MavenBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(MavenBomTool.class)

    static final String POM_FILENAME = 'pom.xml'

    @Autowired
    MavenPackager mavenPackager

    String mvnExecutablePath
    List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.MAVEN
    }

    boolean isBomToolApplicable() {
        mvnExecutablePath = findMavenExecutablePath()
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern(POM_FILENAME)

        mvnExecutablePath && !matchingSourcePaths.empty
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each {
            projectNodes.addAll(mavenPackager.makeDependencyNodes(it, mvnExecutablePath))
        }

        projectNodes
    }

    private String findMavenExecutablePath() {
        if (StringUtils.isBlank(packmanProperties.mavenPath)) {
            return executableManager.getPathOfExecutable(ExecutableType.MVN)
        }
        packmanProperties.mavenPath
    }
}