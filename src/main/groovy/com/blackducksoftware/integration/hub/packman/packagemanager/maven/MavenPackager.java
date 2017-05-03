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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.PackageManagerType;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder;
import com.blackducksoftware.integration.hub.packman.packagemanager.maven.parsers.MavenOutputParser;
import com.blackducksoftware.integration.hub.packman.util.Command;
import com.blackducksoftware.integration.hub.packman.util.CommandRunner;
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer;

public class MavenPackager extends Packager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final boolean aggregateBom;

    private final File sourceDirectory;

    private final ExecutableFinder executableFinder;

    private final List<String> includedScopes;

    private final ProjectInfoGatherer projectInfoGatherer;

    public MavenPackager(final ProjectInfoGatherer projectInfoGatherer, final ExecutableFinder executableFinder, final File sourceDirectory,
            final boolean aggregateBom, final String includedScopesString) {
        this.projectInfoGatherer = projectInfoGatherer;
        this.aggregateBom = aggregateBom;
        this.sourceDirectory = sourceDirectory;
        this.executableFinder = executableFinder;
        this.includedScopes = new ArrayList<>();
        for (final String scope : includedScopesString.split(",")) {
            includedScopes.add(scope.trim().toLowerCase());
        }
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() {
        List<DependencyNode> projects = null;

        final CommandRunner commandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, null);
        final Command mvnCommand = new Command("mvn", "dependency:tree");
        final String mvnOutput = commandRunner.execute(mvnCommand);

        final MavenOutputParser mavenOutputParser = new MavenOutputParser(includedScopes);
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
            firstNode.name = projectInfoGatherer.getProjectName(PackageManagerType.MAVEN, sourceDirectory.getAbsolutePath(), firstNode.name);
            firstNode.version = projectInfoGatherer.getProjectVersion(firstNode.version);
        }

        return projects;
    }
}
