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
package com.blackducksoftware.integration.hub.packman.packagemanager.maven

import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

@Component
public class MavenPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    ExecutableRunner executableRunner

    @ValueDescription(description="If true all maven projects will be aggregated into a single bom")
    @Value('${packman.maven.aggregate}')
    boolean aggregateBom

    @ValueDescription(description="The names of the dependency scopes to include")
    @Value('${packman.maven.scopes.included}')
    String includedScopes

    @ValueDescription(description="The names of the dependency scopes to exclude")
    @Value('${packman.maven.scopes.excluded}')
    String excludedScopes

    ExcludedIncludedFilter excludedIncludedFilter

    @PostConstruct
    void init() {
        excludedIncludedFilter = new ExcludedIncludedFilter(excludedScopes.toLowerCase(), includedScopes.toLowerCase())
    }

    public List<DependencyNode> makeDependencyNodes(String sourcePath, String mavenExecutable) {
        final List<DependencyNode> projects = []

        File sourceDirectory = new File(sourcePath)
        final Executable mvnExecutable = new Executable(sourceDirectory, mavenExecutable, ["dependency:tree"])
        final ExecutableOutput mvnOutput = executableRunner.executeLoudly(mvnExecutable)

        final MavenOutputParser mavenOutputParser = new MavenOutputParser(excludedIncludedFilter)
        projects.addAll(mavenOutputParser.parse(mvnOutput.standardOutput))

        if (aggregateBom && !projects.isEmpty()) {
            final DependencyNode firstNode = projects.remove(0)
            projects.each { subProject ->
                firstNode.children.addAll(subProject.children)
            }
            projects.clear()
            projects.add(firstNode)
            firstNode.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.MAVEN, sourcePath, firstNode.name)
            firstNode.version = projectInfoGatherer.getDefaultProjectVersionName(firstNode.version)
        }

        return projects
    }
}