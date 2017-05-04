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
package com.blackducksoftware.integration.hub.packman.packagemanager.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.PackageManagerType;
import com.blackducksoftware.integration.hub.packman.util.Command;
import com.blackducksoftware.integration.hub.packman.util.CommandRunner;
import com.blackducksoftware.integration.hub.packman.util.FileFinder;
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

public class MavenPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final boolean aggregateBom;

    private final File sourceDirectory;

    private final FileFinder fileFinder;

    private final ExcludedIncludedFilter excludedIncludedFilter;

    private final ProjectInfoGatherer projectInfoGatherer;

    public MavenPackager(final ExcludedIncludedFilter excludedIncludedFilter, final ProjectInfoGatherer projectInfoGatherer,
            final FileFinder fileFinder, final File sourceDirectory, final boolean aggregateBom) {
        this.projectInfoGatherer = projectInfoGatherer;
        this.aggregateBom = aggregateBom;
        this.sourceDirectory = sourceDirectory;
        this.fileFinder = fileFinder;
        this.excludedIncludedFilter = excludedIncludedFilter;
    }

    public List<DependencyNode> makeDependencyNodes() {
        List<DependencyNode> projects = null;

        final CommandRunner commandRunner = new CommandRunner(logger, fileFinder, sourceDirectory, null);
        final Command mvnCommand = new Command("mvn", "dependency:tree");
        final String mvnOutput = commandRunner.execute(mvnCommand);

        final MavenOutputParser mavenOutputParser = new MavenOutputParser(excludedIncludedFilter);
        try {
            projects = mavenOutputParser.parse(mvnOutput);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        if (aggregateBom && !projects.isEmpty()) {
            final DependencyNode firstNode = projects.remove(0);
            projects.forEach(subProject -> firstNode.children.addAll(subProject.children));
            projects.clear();
            projects.add(firstNode);
            firstNode.name = projectInfoGatherer.getDefaultProjectName(PackageManagerType.MAVEN, sourceDirectory.getAbsolutePath(), firstNode.name);
            firstNode.version = projectInfoGatherer.getDefaultProjectVersionName(firstNode.version);
        }

        return projects;
    }
}
