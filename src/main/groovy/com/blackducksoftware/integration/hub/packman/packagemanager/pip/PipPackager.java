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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder;
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.model.PipPackage;
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.parsers.PipShowParser;
import com.blackducksoftware.integration.hub.packman.util.Command;
import com.blackducksoftware.integration.hub.packman.util.CommandRunner;

public class PipPackager extends Packager {
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

    @Override
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
            final Map<String, DependencyNode> allNodes = new HashMap<>();
            final PipShowParser pipShowParser = new PipShowParser();
            final String pipProjectText = pythonCommandRunner.executeQuietly(new Command("pip", "show", projectName));
            final PipPackage projectPackage = pipShowParser.parse(pipProjectText);
            final DependencyNode projectNode = pipPackageToDependencyNode(pipShowParser, pythonCommandRunner, projectPackage, allNodes);
            projects.add(projectNode);
        }

        return projects;
    }

    private DependencyNode pipPackageToDependencyNode(final PipShowParser parser, final CommandRunner pythonCommandRunner, final PipPackage pipPackage,
            final Map<String, DependencyNode> allNodes) {
        final String name = pipPackage.name;
        final String version = pipPackage.version;
        final ExternalId externalId = new NameVersionExternalId(Forge.pypi, name, version);
        final DependencyNode projectNode = new DependencyNode(name, version, externalId);

        if (pipPackage.requires != null) {
            for (final String dependency : pipPackage.requires) {
                if (allNodes.containsKey(dependency.toLowerCase())) {
                    final DependencyNode dependencyNode = allNodes.get(dependency.toLowerCase());
                    projectNode.children.add(dependencyNode);
                } else {
                    final String pipProjectText = pythonCommandRunner.executeQuietly(new Command("pip", "show", dependency));
                    final PipPackage dependencyPackage = parser.parse(pipProjectText);
                    final DependencyNode dependencyNode = pipPackageToDependencyNode(parser, pythonCommandRunner, dependencyPackage, allNodes);
                    allNodes.put(dependencyNode.name.toLowerCase(), dependencyNode);
                    projectNode.children.add(dependencyNode);
                }
            }
        }
        allNodes.put(name.toLowerCase(), projectNode);
        return projectNode;
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
