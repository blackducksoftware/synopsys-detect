/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow.file;

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

    public AirGapManager(AirGapOptions airGapOptions) {
        File detectJar = null;
        try {
            detectJar = new File(guessDetectJarLocation()).getCanonicalFile();
        } catch (IOException e) {
            logger.debug("Unable to guess detect jar location.");
        }
        dockerInspectorAirGapPath = getInspectorAirGapPath(detectJar, airGapOptions.getDockerInspectorPathOverride(), DOCKER);
        gradleInspectorAirGapPath = getInspectorAirGapPath(detectJar, airGapOptions.getGradleInspectorPathOverride(), GRADLE);
        nugetInspectorAirGapPath = getInspectorAirGapPath(detectJar, airGapOptions.getNugetInspectorPathOverride(), NUGET);
    }

    private String getInspectorAirGapPath(File detectJar, final String inspectorLocationProperty, final String inspectorName) {
        if (StringUtils.isBlank(inspectorLocationProperty) && detectJar != null) {
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
