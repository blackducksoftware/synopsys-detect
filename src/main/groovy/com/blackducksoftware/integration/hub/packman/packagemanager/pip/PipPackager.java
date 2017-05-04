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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.PackmanProperties;
import com.blackducksoftware.integration.hub.packman.util.Command;
import com.blackducksoftware.integration.hub.packman.util.CommandRunner;
import com.blackducksoftware.integration.hub.packman.util.FileFinder;

@Component
public class PipPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FileFinder fileFinder;

    @Autowired
    PackmanProperties packmanProperties;

    @Value("${packman.pip.createVirtualEnv}")
    boolean createVirtualEnv;

    @Value("${packman.pip.pip3}")
    boolean pipThreeOverride;

    private String virtualEnvBin = "bin";

    private String pip = "pip";

    private String python = "python";

    public List<DependencyNode> makeDependencyNodes(final String sourcePath) throws IOException {
        final List<DependencyNode> projects = new ArrayList<>();
        if (pipThreeOverride) {
            pip += "3";
            python += "3";
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            virtualEnvBin = "Scripts";
            pip += ".exe";
            python += ".exe";
        }

        final File sourceDirectory = new File(sourcePath);
        final CommandRunner systemCommandRunner = new CommandRunner(logger, fileFinder, sourceDirectory, null);
        final CommandRunner pythonCommandRunner = getPythonCommandRunner(systemCommandRunner, sourceDirectory, virtualEnvBin);

        final Command installProject = new Command(pip, "install", ".");
        final Command getProjectName = new Command(python, "setup.py", "--name");

        pythonCommandRunner.execute(installProject);

        logger.info("Running PIP analysis");
        final String projectName = pythonCommandRunner.executeQuietly(getProjectName).trim();
        if (projectName.equals("UNKOWN")) {
            logger.error("Could not determine project name. Please make sure it is specified in your setup.py");
        } else {
            final PipShowParser pipShowParser = new PipShowParser();
            final String pipProjectText = pythonCommandRunner.executeQuietly(new Command(pip, "show", projectName));
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

        final String pipProjectText = pythonCommandRunner.executeQuietly(new Command(pip, "show", rawDependencyNode.name));
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

    private CommandRunner getPythonCommandRunner(final CommandRunner systemCommandRunner, final File sourceDirectory, final String virtualEnvBin) {
        CommandRunner pythonCommandRunner = null;
        if (createVirtualEnv) {
            final File virtualEnvironmentPath = new File(packmanProperties.getOutputDirectoryPath(), "blackduck_virtualenv");
            final File virtualEnvironmentBinPath = new File(virtualEnvironmentPath, virtualEnvBin);

            pythonCommandRunner = new CommandRunner(logger, fileFinder, sourceDirectory, null, virtualEnvironmentBinPath.getAbsolutePath());

            final String pythonPath = fileFinder.findExecutablePath(python);
            final Command installVirtualenvPackage = new Command(pip, "install", "virtualenv");
            final Command createVirtualEnvironement = new Command("virtualenv", "-p", pythonPath, virtualEnvironmentPath.getAbsolutePath());

            if (virtualEnvironmentPath.exists() && virtualEnvironmentBinPath.exists()
                    && fileFinder.findExecutablePath(pip, virtualEnvironmentBinPath.getAbsolutePath()) != null) {
                logger.info(String.format("Found virtual environment: %s", virtualEnvironmentPath.getAbsolutePath()));
            } else {
                systemCommandRunner.execute(installVirtualenvPackage);
                systemCommandRunner.execute(createVirtualEnvironement);
            }
        } else {
            pythonCommandRunner = new CommandRunner(logger, fileFinder, sourceDirectory, null);
        }
        return pythonCommandRunner;
    }
}
