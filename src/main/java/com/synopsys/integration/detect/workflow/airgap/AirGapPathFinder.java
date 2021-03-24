/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirGapPathFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String NUGET = "nuget";
    public static final String GRADLE = "gradle";
    public static final String DOCKER = "docker";

    public File findDetectJar() {
        try {
            final String relativeJarFile = guessJarInvocation();
            if (relativeJarFile == null) {
                logger.debug("Unable to guess detect jar file, relative jar file was null.");
                return null;
            } else {
                final File jarFile = new File(relativeJarFile).getCanonicalFile();
                logger.debug("Checking for jar file: " + jarFile.toString());
                if (jarFile.exists()) {
                    logger.debug("Found detect jar file.");
                    return jarFile;
                } else {
                    logger.debug("No detect jar file could be found.");
                    return null;
                }
            }
        } catch (final IOException e) {
            logger.debug("An error occurred while guessing detect jar location.");
            return null;
        }
    }

    public File createRelativePackagedInspectorsFile(final File file, final String inspectorName) {
        final File packagedInspectorsFolder = new File(file, "packaged-inspectors");
        final File inspectorFolder = new File(packagedInspectorsFolder, inspectorName);
        return inspectorFolder;
    }

    // This will attempt to guess the relative path to the detect jar, ie what is passed to java -jar {here}
    private String guessJarInvocation() {
        final String containsDetectJarRegex = ".*synopsys-detect-[^\\\\/]+\\.jar.*";
        final String javaClasspath = System.getProperty("java.class.path");
        if (javaClasspath != null && javaClasspath.matches(containsDetectJarRegex)) {
            for (final String classpathChunk : javaClasspath.split(System.getProperty("path.separator"))) {
                if (classpathChunk != null && classpathChunk.matches(containsDetectJarRegex)) {
                    logger.debug(String.format("Guessed Detect jar location as %s", classpathChunk));
                    return classpathChunk;
                }
            }
        }
        return null;
    }
}
