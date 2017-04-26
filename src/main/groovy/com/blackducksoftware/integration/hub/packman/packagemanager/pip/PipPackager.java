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

    public PipPackager(final ExecutableFinder executableFinder, final String sourceDirectory, final String outputDirectory, final boolean createVirtualEnv) {
        this.executableFinder = executableFinder;
        this.sourceDirectory = new File(sourceDirectory);
        this.outputDirectory = new File(outputDirectory);
        this.createVirtualEnv = createVirtualEnv;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() throws IOException {
        createVirtualEnvironment();
        return null;
    }

    private void createVirtualEnvironment() {
        if (createVirtualEnv) {
            String virtualEnvBin = "bin";

            Map<String, String> windowsFileMap = new HashMap<>();
            windowsFileMap.put(virtualEnvBin, "Scripts");
            windowsFileMap.put("pip", "pip.exe");
            windowsFileMap.put("python", "python.exe");

            if (SystemUtils.IS_OS_WINDOWS) {
                virtualEnvBin = windowsFileMap.get(virtualEnvBin);
            } else {
                windowsFileMap = null;
            }

            final File virtualEnvironmentPath = new File(outputDirectory, "blackduck_virtualenv");
            final String virtualEnvironmentBinPath = new File(virtualEnvironmentPath, virtualEnvBin).getAbsolutePath();
            final CommandRunner systemCommandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, windowsFileMap);
            final CommandRunner virtualenvCommandRunner = new CommandRunner(logger, executableFinder, sourceDirectory, windowsFileMap,
                    virtualEnvironmentBinPath);

            final Command installVirtualenvPackage = new Command("pip", "install", "virtualenv");
            final Command createVirtualEnvironement = new Command("virtualenv", virtualEnvironmentPath.getAbsolutePath());
            final Command installHubPip = new Command("pip", "install", "hub-pip");
            final Command installProject = new Command("pip", "install", ".");
            final Command runHubPip = new Command("python", "setup.py", "hub_pip",
                    "--CreateTreeDependencyList=True",
                    "--CreateHubBdio=False",
                    "--OutputDirectory=" + outputDirectory.getAbsolutePath());

            if (!virtualEnvironmentPath.exists()) {
                systemCommandRunner.execute(installVirtualenvPackage);
                systemCommandRunner.execute(createVirtualEnvironement);
            } else {
                logger.info(String.format("Found virtual environment: %s", virtualEnvironmentPath.getAbsolutePath()));
            }
            systemCommandRunner.execute(installHubPip);
            virtualenvCommandRunner.execute(installProject);
            virtualenvCommandRunner.execute(runHubPip);
        }
    }
}
