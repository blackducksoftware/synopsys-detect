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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.commands.Command
import com.blackducksoftware.integration.hub.packman.util.commands.CommandOutput
import com.blackducksoftware.integration.hub.packman.util.commands.CommandRunner
import com.blackducksoftware.integration.hub.packman.util.commands.Executable
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

public class MavenPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    private final boolean aggregateBom

    private final File sourceDirectory

    private final ExcludedIncludedFilter excludedIncludedFilter

    private final ProjectInfoGatherer projectInfoGatherer

    private final Map<String, Executable> executables

    public MavenPackager(final ExcludedIncludedFilter excludedIncludedFilter, final ProjectInfoGatherer projectInfoGatherer, final File sourceDirectory,
    final boolean aggregateBom, final Map<String, Executable> executables) {
        this.projectInfoGatherer = projectInfoGatherer
        this.aggregateBom = aggregateBom
        this.sourceDirectory = sourceDirectory
        this.excludedIncludedFilter = excludedIncludedFilter
        this.executables = executables
    }

    public List<DependencyNode> makeDependencyNodes() {
        final List<DependencyNode> projects = new ArrayList<>()

        final CommandRunner commandRunner = new CommandRunner(logger, sourceDirectory)
        final Command mvnCommand = new Command(executables.get("mvn"), "dependency:tree")
        final CommandOutput mvnOutput = commandRunner.execute(mvnCommand)

        final MavenOutputParser mavenOutputParser = new MavenOutputParser(excludedIncludedFilter)

        if (!mvnOutput.hasErrors()) {
            projects.addAll(mavenOutputParser.parse(mvnOutput.output))
        } else {
            logger.warn(String.format("Executing %s", mvnCommand.getExecutable().getOriginal()))
        }

        if (aggregateBom && !projects.isEmpty()) {
            final DependencyNode firstNode = projects.remove(0)
            projects.each { subProject ->
                firstNode.children.addAll(subProject.children)
            }
            projects.clear()
            projects.add(firstNode)
            firstNode.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.MAVEN, sourceDirectory.getAbsolutePath(), firstNode.name)
            firstNode.version = projectInfoGatherer.getDefaultProjectVersionName(firstNode.version)
        }

        return projects
    }
}