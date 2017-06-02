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
package com.blackducksoftware.integration.hub.packman.bomtool.pip;

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
import com.blackducksoftware.integration.hub.packman.util.executable.Executable;
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunnerException;

@Component
public class PipPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExecutableManager executableManager;

    @Autowired
    ExecutableRunner executableRunner;

    @Autowired
    PipShowMapParser pipShowMapParser;

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, final String pipExecutable, final String pythonExecutable,
            final Map<String, String> environmentVariables)
            throws ExecutableRunnerException {
        final List<DependencyNode> projects = new ArrayList<>();

        final File sourceDirectory = new File(sourcePath);
        final Executable installProject = new Executable(sourceDirectory, environmentVariables, pipExecutable, Arrays.asList("install", "."));
        executableRunner.executeLoudly(installProject);

        final File setupScript = new File(sourceDirectory, "setup.py");
        final List<String> projectNameArgs = Arrays.asList(setupScript.getAbsolutePath(), "--name");
        final Executable projectNameExecutable = new Executable(sourceDirectory, environmentVariables, pythonExecutable, projectNameArgs);
        final String projectName = executableRunner.executeLoudly(projectNameExecutable).getStandardOutput().trim();

        logger.info("Running PIP analysis");
        if (StringUtils.isBlank(projectName)) {
            logger.error("Could not determine project name. Please make sure it is specified in your setup.py");
        } else {
            final Executable pipShowExecutable = new Executable(sourceDirectory, environmentVariables, pipExecutable, Arrays.asList("show", projectName));
            final ExecutableOutput pipProjectOutput = executableRunner.executeQuietly(pipShowExecutable);
            final Map<String, String> projectPipShowMap = pipShowMapParser.parse(pipProjectOutput.getStandardOutput());
            final DependencyNode projectNode = pipShowMapToNode(projectPipShowMap);
            projectNode.children.clear();
            final DependencyNodeBuilder nodeBuilder = new DependencyNodeBuilder(projectNode);
            final Map<String, DependencyNode> allNodes = new HashMap<>();
            dependencyNodeTransformer(sourceDirectory, projectNode, nodeBuilder, allNodes, pipExecutable, environmentVariables);
            projects.add(projectNode);
        }
        return projects;
    }

    private DependencyNode dependencyNodeTransformer(final File sourceDirectory, final DependencyNode rawDependencyNode,
            final DependencyNodeBuilder nodeBuilder, final Map<String, DependencyNode> allNodes, final String pipExecutable,
            final Map<String, String> environmentVariables) throws ExecutableRunnerException {
        if (allNodes.containsKey(rawDependencyNode.name.toLowerCase())) {
            return allNodes.get(rawDependencyNode.name.toLowerCase());
        }
        final Executable pipShowExecutable = new Executable(sourceDirectory, environmentVariables, pipExecutable,
                Arrays.asList("show", rawDependencyNode.name));
        final String pipProjectText = executableRunner.executeQuietly(pipShowExecutable).getStandardOutput();
        final DependencyNode dependencyNode = pipShowMapToNode(pipShowMapParser.parse(pipProjectText));

        final List<DependencyNode> children = new ArrayList<>();
        for (final DependencyNode rawChildNode : dependencyNode.children) {
            children.add(dependencyNodeTransformer(sourceDirectory, rawChildNode, nodeBuilder, allNodes, pipExecutable, environmentVariables));
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
        final ExternalId externalId = new NameVersionExternalId(Forge.PYPI, name, version);
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
