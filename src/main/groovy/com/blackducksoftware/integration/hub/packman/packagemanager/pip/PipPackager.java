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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.util.command.Command;
import com.blackducksoftware.integration.hub.packman.util.command.CommandManager;
import com.blackducksoftware.integration.hub.packman.util.command.CommandOutput;
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunner;
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunnerException;

@Component
public class PipPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CommandManager commandManager;

    @Autowired
    CommandRunner commandRunner;

    @Autowired
    PipShowMapParser pipShowMapParser;

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, final String pipCommand, final String pythonCommand,
            final Map<String, String> environmentVariables)
            throws CommandRunnerException {
        final List<DependencyNode> projects = new ArrayList<>();

        final File sourceDirectory = new File(sourcePath);
        final Command installProject = new Command(sourceDirectory, environmentVariables, pipCommand, Arrays.asList("install", "."));

        final CommandOutput installOutput = commandRunner.executeLoudly(installProject);
        String projectName = null;
        for (final String line : installOutput.getStandardOutput().split("\n")) {
            if (line.contains("Successfully built")) {
                projectName = line.replace("Successfully built", "").trim();
            }
        }

        logger.info("Running PIP analysis");
        if (StringUtils.isBlank(projectName)) {
            logger.error("Could not determine project name. Please make sure it is specified in your setup.py");
        } else {
            final Command pipShowCommand = new Command(sourceDirectory, environmentVariables, pipCommand, Arrays.asList("show", projectName));
            final CommandOutput pipProjectOutput = commandRunner.executeQuietly(pipShowCommand);
            final Map<String, String> projectPipShowMap = pipShowMapParser.parse(pipProjectOutput.getStandardOutput());
            final DependencyNode projectNode = pipShowMapToNode(projectPipShowMap);
            projectNode.children.clear();
            final DependencyNodeBuilder nodeBuilder = new DependencyNodeBuilder(projectNode);
            final Map<String, DependencyNode> allNodes = new HashMap<>();
            dependencyNodeTransformer(sourceDirectory, projectNode, nodeBuilder, allNodes, pipCommand, environmentVariables);
            projects.add(projectNode);
        }
        return projects;
    }

    private DependencyNode dependencyNodeTransformer(final File sourceDirectory, final DependencyNode rawDependencyNode,
            final DependencyNodeBuilder nodeBuilder, final Map<String, DependencyNode> allNodes, final String pipCommand,
            final Map<String, String> environmentVariables) throws CommandRunnerException {
        if (allNodes.containsKey(rawDependencyNode.name.toLowerCase())) {
            return allNodes.get(rawDependencyNode.name.toLowerCase());
        }
        final Command pipShowCommand = new Command(sourceDirectory, environmentVariables, pipCommand, Arrays.asList("show", rawDependencyNode.name));
        final String pipProjectText = commandRunner.executeQuietly(pipShowCommand).getStandardOutput();
        final DependencyNode dependencyNode = pipShowMapToNode(pipShowMapParser.parse(pipProjectText));

        final List<DependencyNode> children = new ArrayList<>();
        for (final DependencyNode rawChildNode : dependencyNode.children) {
            children.add(dependencyNodeTransformer(sourceDirectory, rawChildNode, nodeBuilder, allNodes, pipCommand, environmentVariables));
        }
        dependencyNode.children.clear();
        nodeBuilder.addParentNodeWithChildren(dependencyNode, children);
        allNodes.put(dependencyNode.name.toLowerCase(), dependencyNode);
        return dependencyNode;
    }

    private DependencyNode pipShowMapToNode(final Map<String, String> pipShowMap) {
        final String name = pipShowMap.get("Name").trim();
        final String version = pipShowMap.get("Version").trim();
        final String[] requires = pipShowMap.get("Requires").split(",");
        final ExternalId externalId = new NameVersionExternalId(Forge.pypi, name, version);
        final DependencyNode node = new DependencyNode(name, version, externalId);
        for (final String requirement : requires) {
            if (StringUtils.isNotBlank(requirement)) {
                final DependencyNode child = new DependencyNode(requirement.trim(), "", null);
                node.children.add(child);
            }
        }
        return node;
    }
}
