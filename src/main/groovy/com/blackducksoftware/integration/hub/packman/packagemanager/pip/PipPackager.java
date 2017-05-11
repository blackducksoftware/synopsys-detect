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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.util.commands.Command;
import com.blackducksoftware.integration.hub.packman.util.commands.CommandRunner;
import com.blackducksoftware.integration.hub.packman.util.commands.Executable;

@Component
public class PipPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, final Map<String, Executable> executables) {
        final List<DependencyNode> projects = new ArrayList<>();

        final File sourceDirectory = new File(sourcePath);
        final CommandRunner commandRunner = new CommandRunner(logger, sourceDirectory);

        final Command installProject = new Command(executables.get("pip"), "install", ".");
        final Command getProjectName = new Command(executables.get("python"), "setup.py", "--name");

        commandRunner.execute(installProject);

        logger.info("Running PIP analysis");
        final String projectName = commandRunner.executeQuietly(getProjectName).getOutputStream().trim();
        if (projectName.equals("UNKOWN")) {
            logger.error("Could not determine project name. Please make sure it is specified in your setup.py");
        } else {
            final PipShowParser pipShowParser = new PipShowParser();
            final String pipProjectText = commandRunner.executeQuietly(new Command(executables.get("pip"), "show", projectName)).getOutputStream();
            final DependencyNode projectNode = pipShowParser.parse(pipProjectText);
            projectNode.children.clear();
            final DependencyNodeBuilder nodeBuilder = new DependencyNodeBuilder(projectNode);
            final Map<String, DependencyNode> allNodes = new HashMap<>();
            dependencyNodeTransformer(pipShowParser, commandRunner, projectNode, nodeBuilder, allNodes, executables);
            projects.add(projectNode);
        }
        return projects;
    }

    private DependencyNode dependencyNodeTransformer(final PipShowParser parser, final CommandRunner pythonCommandRunner,
            final DependencyNode rawDependencyNode, final DependencyNodeBuilder nodeBuilder, final Map<String, DependencyNode> allNodes,
            final Map<String, Executable> executables) {
        if (allNodes.containsKey(rawDependencyNode.name.toLowerCase())) {
            return allNodes.get(rawDependencyNode.name.toLowerCase());
        }

        final String pipProjectText = pythonCommandRunner.executeQuietly(new Command(executables.get("pip"), "show", rawDependencyNode.name)).getOutputStream();
        final DependencyNode dependencyNode = parser.parse(pipProjectText);

        final List<DependencyNode> children = new ArrayList<>();
        for (final DependencyNode rawChildNode : dependencyNode.children) {
            children.add(dependencyNodeTransformer(parser, pythonCommandRunner, rawChildNode, nodeBuilder, allNodes, executables));
        }
        dependencyNode.children.clear();
        nodeBuilder.addParentNodeWithChildren(dependencyNode, children);
        allNodes.put(dependencyNode.name.toLowerCase(), dependencyNode);
        return dependencyNode;
    }
}
