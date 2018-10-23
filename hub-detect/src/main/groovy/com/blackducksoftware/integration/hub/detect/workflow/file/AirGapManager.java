package com.blackducksoftware.integration.hub.detect.workflow.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirGapManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String NUGET = "nuget";
    public static final String GRADLE = "gradle";
    public static final String DOCKER = "docker";

    private String dockerInspectorAirGapPath;
    private String nugetInspectorAirGapPath;
    private String gradleInspectorAirGapPath;

    public void init(AirGapOptions airGapOptions) throws IOException {
        final File detectJar = new File(guessDetectJarLocation()).getCanonicalFile();
        dockerInspectorAirGapPath = getInspectorAirGapPath(detectJar, airGapOptions.getDockerInspectorPathOverride(), DOCKER);
        gradleInspectorAirGapPath = getInspectorAirGapPath(detectJar, airGapOptions.getGradleInspectorPathOverride(), GRADLE);
        nugetInspectorAirGapPath = getInspectorAirGapPath(detectJar, airGapOptions.getNugetInspectorPathOverride(), NUGET);
    }

    private String getInspectorAirGapPath(File detectJar, final String inspectorLocationProperty, final String inspectorName) {
        if (StringUtils.isBlank(inspectorLocationProperty)) {
            try {

                final File inspectorsDirectory = new File(detectJar.getParentFile(), "packaged-inspectors");
                final File inspectorAirGapDirectory = new File(inspectorsDirectory, inspectorName);
                return inspectorAirGapDirectory.getCanonicalPath();
            } catch (final Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for %s, returning the detect property instead", inspectorName));
                logger.debug(e.getMessage());
            }
        }
        return inspectorLocationProperty;
    }

    private String guessDetectJarLocation() {
        final String containsDetectJarRegex = ".*hub-detect-[^\\\\/]+\\.jar.*";
        final String javaClasspath = System.getProperty("java.class.path");
        if (javaClasspath != null && javaClasspath.matches(containsDetectJarRegex)) {
            for (final String classpathChunk : javaClasspath.split(System.getProperty("path.separator"))) {
                if (classpathChunk != null && classpathChunk.matches(containsDetectJarRegex)) {
                    logger.debug(String.format("Guessed Detect jar location as %s", classpathChunk));
                    return classpathChunk;
                }
            }
        }
        return "";
    }

    public String getDockerInspectorAirGapPath() {
        return dockerInspectorAirGapPath;
    }

    public String getNugetInspectorAirGapPath() {
        return nugetInspectorAirGapPath;
    }

    public String getGradleInspectorAirGapPath() {
        return gradleInspectorAirGapPath;
    }
}
