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
package com.blackducksoftware.integration.hub.packman.parser.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Map;

import org.assertj.core.internal.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.parser.Packager;
import com.blackducksoftware.integration.hub.packman.parser.maven.parsers.MavenOutputParser;

import okhttp3.internal.platform.Platform;

public class MavenPackager extends Packager {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final boolean aggregateBom;

	private final String sourceDirectory;

	public MavenPackager(final String sourceDirectory, final boolean aggregateBom) {
		this.sourceDirectory = sourceDirectory;
		this.aggregateBom = aggregateBom;
	}

	@Override
	public List<DependencyNode> makeDependencyNodes() {
		InputStream mavenOutputFileStream = null;
		try {
			final File mavenOutputFile = File.createTempFile("mavenOutputStream", ".tmp");
			logger.info("writing maven outputsteram to " + mavenOutputFile.getAbsolutePath());
			final File sourceDirectoryFile = new File(sourceDirectory);

			String mavenPath = null;
			try {
				mavenPath = findExecutable("mvn"); // TODO: Create enum
			} catch (Exception ignore) {
			}

			if (mavenPath == null)
				mavenPath = System.getenv("M2");

			final ProcessBuilder processBuilder = new ProcessBuilder(mavenPath, "dependency:tree");

			processBuilder.directory(sourceDirectoryFile);
			processBuilder.redirectOutput(Redirect.to(mavenOutputFile));

			logger.info("running mvn dependency:tree");
			final Process process = processBuilder.start();

			try {
				process.waitFor();
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}

			logger.info("parsing maven's output stream");
			final MavenOutputParser mavenParser = new MavenOutputParser();
			mavenOutputFileStream = new FileInputStream(mavenOutputFile);
			final List<DependencyNode> projects = mavenParser.parse(mavenOutputFileStream);

			logger.info("cleaning up tempory files");
			mavenOutputFile.delete();

			if (aggregateBom && !projects.isEmpty()) {
				final DependencyNode firstNode = projects.remove(0);
				for (final DependencyNode subProject : projects) {
					firstNode.children.addAll(subProject.children);
				}
				projects.clear();
				projects.add(firstNode);
			}

			return projects;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				mavenOutputFileStream.close();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			} catch (final NullPointerException e) {

			}
		}
	}

	private String findExecutable(String executable) throws Exception {
		String path = null;
		String command = "which";
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			command = "where";
		}
		final ProcessBuilder processBuilder = new ProcessBuilder(command, executable);

		final Process process = processBuilder.start();
		try {

			int errCode = process.waitFor();
			if (errCode == 0) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line = null;
				while ((line = reader.readLine()) != null) {
					path = (line); // the last line will be the execution line
				}
			}
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (path == null)
			throw new Exception("Unable to determine path for: " + executable);
		return path;
	}

}
