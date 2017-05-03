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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.util.Command;
import com.blackducksoftware.integration.hub.packman.util.CommandRunner;
import com.blackducksoftware.integration.hub.packman.util.ExecutableFinder;

public class PipPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableFinder executableFinder;

    private final File sourceDirectory;

    private final File outputDirectory;

    boolean createVirtualEnv;

    private Map<String, String> windowsFileMap;

    private String virtualEnvBin = "bin";

    public PipPackager(final ExecutableFinder executableFinder, final String sourceDirectory, final String outputDirectory, final boolean createVirtualEnv) {
        this.executableFinder = executableFinder;
        this.sourceDirectory = new File(sourceDirectory);
        this.outputDirectory = new File(outputDirectory);
        this.createVirtualEnv = createVirtualEnv;

        this.windowsFileMap = new HashMap<>();
        windowsFileMap.put(virtualEnvBin, "Scripts");
        windowsFileMap.put("pip", "pip.exe");
        windowsFileMap.put("python", "python.exe");
    }

    public List<DependencyNode> makeDependencyNodes() throws IOException {
        final List<DependencyNode> projects = new ArrayList<>();

        if (SystemUtils.IS_OS_WINDOWS) {
            virtualEnvBin = windowsFileMap.get(virtualEnvBin);
        } else {
            windowsFileMap = null;
        }

        final CommandRunner systemCommandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, windowsFileMap);
        final CommandRunner pythonCommandRunner = getPythonCommandRunner(systemCommandRunner, createVirtualEnv);

        final Command installProject = new Command("pip", "install", ".");
        final Command getProjectName = new Command("python", "setup.py", "--name");

        pythonCommandRunner.execute(installProject);

        logger.info("Running PIP analysis");
        final String projectName = pythonCommandRunner.executeQuietly(getProjectName).trim();
        if (projectName.equals("UNKOWN")) {
            logger.error("Could not determine project name. Please make sure it is specified in your setup.py");
        } else {
            final PipShowParser pipShowParser = new PipShowParser();
            final String pipProjectText = pythonCommandRunner.executeQuietly(new Command("pip", "show", projectName));
            final DependencyNode projectNode = pipShowParser.parse(pipProjectText);
            projectNode.children.clear();
            final DependencyNodeBuilder nodeBuilder = new DependencyNodeBuilder(projectNode);
            final Map<String, DependencyNode> allNodes = new HashMap<>();
            dependencyNodeTransformer(pipShowParser, pythonCommandRunner, projectNode, nodeBuilder, allNodes);
            projects.add(nodeBuilder.buildRootNode());
        }
        return projects;
    }

    private DependencyNode dependencyNodeTransformer(final PipShowParser parser, final CommandRunner pythonCommandRunner,
            final DependencyNode rawDependencyNode, final DependencyNodeBuilder nodeBuilder, final Map<String, DependencyNode> allNodes) {
        if (allNodes.containsKey(rawDependencyNode.name.toLowerCase())) {
            return allNodes.get(rawDependencyNode.name.toLowerCase());
        }

        final String pipProjectText = pythonCommandRunner.executeQuietly(new Command("pip", "show", rawDependencyNode.name));
        final DependencyNode dependencyNode = parser.parse(pipProjectText);

        final List<DependencyNode> children = new ArrayList<>();
        for (final DependencyNode rawChildNode : dependencyNode.children) {
            children.add(dependencyNodeTransformer(parser, pythonCommandRunner, rawChildNode, nodeBuilder, allNodes));
        }
        dependencyNode.children.clear();
        nodeBuilder.addParentNodeWithChildren(dependencyNode, children);
        allNodes.put(dependencyNode.name.toLowerCase(), dependencyNode);
        return dependencyNode;
    }

    private CommandRunner getPythonCommandRunner(final CommandRunner systemCommandRunner, final boolean createVirtualEnvironment) {
        CommandRunner pythonCommandRunner = null;
        if (createVirtualEnv) {
            final File virtualEnvironmentPath = new File(outputDirectory, "blackduck_virtualenv");
            final File virtualEnvironmentBinPath = new File(virtualEnvironmentPath, virtualEnvBin);

            pythonCommandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, windowsFileMap, virtualEnvironmentBinPath.getAbsolutePath());

            final Command installVirtualenvPackage = new Command("pip", "install", "virtualenv");
            final Command createVirtualEnvironement = new Command("virtualenv", virtualEnvironmentPath.getAbsolutePath());

            if (virtualEnvironmentPath.exists() && virtualEnvironmentBinPath.exists()) {
                logger.info(String.format("Found virtual environment: %s", virtualEnvironmentPath.getAbsolutePath()));
            } else {
                systemCommandRunner.execute(installVirtualenvPackage);
                systemCommandRunner.execute(createVirtualEnvironement);
            }
        } else {
            pythonCommandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, windowsFileMap);
        }
        return pythonCommandRunner;
    }
}
