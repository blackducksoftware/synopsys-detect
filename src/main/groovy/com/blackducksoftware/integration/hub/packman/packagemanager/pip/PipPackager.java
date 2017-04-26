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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder;
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

        final CommandRunner systemCommandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, windowsFileMap);
        final CommandRunner pythonCommandRunner = getPythonCommandRunner(systemCommandRunner, createVirtualEnv);

        final Command installProject = new Command("pip", "install", ".");
        final Command getProjectName = new Command("python", "setup.py", "--name");

        pythonCommandRunner.execute(installProject);

        final String projectName = pythonCommandRunner.executeQuietly(getProjectName).trim();
        if (projectName.equals("UNKOWN")) {
            logger.error("Could not determine project name. Please make sure it is specified in your setup.py");
        } else {
            pythonCommandRunner.execute(new Command("pip", "show", projectName));
        }
        return null;
    }

    private CommandRunner getPythonCommandRunner(final CommandRunner systemCommandRunner, final boolean createVirtualEnvironment) {
        CommandRunner pythonCommandRunner = null;
        if (createVirtualEnv) {
            if (SystemUtils.IS_OS_WINDOWS) {
                virtualEnvBin = windowsFileMap.get(virtualEnvBin);
            } else {
                windowsFileMap = null;
            }

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
