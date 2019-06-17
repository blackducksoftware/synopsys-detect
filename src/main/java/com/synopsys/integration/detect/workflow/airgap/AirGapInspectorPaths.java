/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirGapInspectorPaths {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String dockerInspectorAirGapPath;
    private final String nugetInspectorAirGapPath;
    private final String gradleInspectorAirGapPath;

    public AirGapInspectorPaths(AirGapPathFinder pathFinder, final AirGapOptions airGapOptions) {
        File detectJar = pathFinder.findDetectJar();
        dockerInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getDockerInspectorPathOverride(), AirGapPathFinder.DOCKER);
        gradleInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getGradleInspectorPathOverride(), AirGapPathFinder.GRADLE);
        nugetInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getNugetInspectorPathOverride(), AirGapPathFinder.NUGET);
    }

    private String determineInspectorAirGapPath(final File detectJar, AirGapPathFinder airGapPathFinder, final String inspectorLocationProperty, final String inspectorName) {
        if (StringUtils.isBlank(inspectorLocationProperty) && detectJar != null) {
            try {
                return airGapPathFinder.createRelativePackagedInspectorsFile(detectJar.getParentFile(), inspectorName).getCanonicalPath();
            } catch (final Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for %s, returning the detect property instead", inspectorName));
                logger.debug(e.getMessage());
            }
        }
        return inspectorLocationProperty;
    }

    public String getDockerInspectorAirGapPath() {
        return dockerInspectorAirGapPath;
    }

    private String getNugetInspectorAirGapPath() {
        return nugetInspectorAirGapPath;
    }

    private String getGradleInspectorAirGapPath() {
        return gradleInspectorAirGapPath;
    }

    public Optional<File> getNugetInspectorAirGapFile() {
        return getFileFromPath(getNugetInspectorAirGapPath());
    }

    public Optional<File> getDockerInspectorAirGapFile() {
        return getFileFromPath(getDockerInspectorAirGapPath());
    }

    public Optional<File> getGradleInspectorAirGapFile() {
        return getFileFromPath(getGradleInspectorAirGapPath());
    }

    private Optional<File> getFileFromPath(final String path) {
        return Optional.ofNullable(path)
                   .filter(StringUtils::isNotBlank)
                   .map(File::new)
                   .filter(File::exists);
    }
}
