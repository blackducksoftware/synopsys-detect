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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.packman.parser.Packager;
import com.blackducksoftware.integration.hub.packman.parser.maven.parsers.MavenOutputParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MavenPackager extends Packager {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String sourceDirectory;

    public MavenPackager(final String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    @Override
    public List<DependencyNode> makeDependencyNodes() {
        InputStream mavenOutputFileStream = null;
        try {
            final File mavenOutputFile = File.createTempFile("mavenOutputStream", ".tmp");
            logger.info("writing maven outputsteram to " + mavenOutputFile.getAbsolutePath());
            final File sourceDirectoryFile = new File(sourceDirectory);

            final ProcessBuilder processBuilder = new ProcessBuilder(System.getenv("M2"), "dependency:tree");
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

}
