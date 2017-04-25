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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.Packager;
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder;
import com.blackducksoftware.integration.hub.packman.util.Command;
import com.blackducksoftware.integration.hub.packman.util.InputStreamConverter;

public class PipPackager extends Packager {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	final InputStreamConverter inputStreamConverter;

	final ExecutableFinder executableFinder;

	final File sourceDirectory;

	File outputDirectory;

	boolean createVirtualEnv;

	public PipPackager(final InputStreamConverter inputStreamConverter, final ExecutableFinder executableFinder,
			final String sourceDirectory, final String outputDirectory, final boolean createVirtualEnv) {
		this.inputStreamConverter = inputStreamConverter;
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
		String executionFolder = "bin";
		String pipExecutable = "pip";
		String pythonExecutable = "python";
		if (createVirtualEnv) {
			final File virtualEnvironmentPath = new File(outputDirectory, "blackduck_virtualenv");

			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				executionFolder = "Scripts";
				pipExecutable += ".exe";
				pythonExecutable += ".exe";
			}
			final File virtualEnvironmentBinPath = new File(virtualEnvironmentPath, executionFolder);
			final Command command = new Command(logger, executableFinder, sourceDirectory);

			command.execute("pip", "install", "virtualenv");
			command.execute("virtualenv", virtualEnvironmentPath.getAbsolutePath());

			// TODO:  We needed to append the .exe, otherwise executables are picked up from global Windows path. No bueno.
			command.execute(virtualEnvironmentBinPath, pipExecutable, "install", "hub-pip");
			command.execute(virtualEnvironmentBinPath, pythonExecutable, "setup.py", "install");
			command.execute(virtualEnvironmentBinPath, pythonExecutable, "setup.py", "hub_pip",
					"--CreateTreeDependencyList=True", "--CreateHubBdio=False",
					"--OutputDirectory=" + outputDirectory.getAbsolutePath());

		}

	}

}
