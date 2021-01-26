/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirGapInspectorPaths {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Path dockerInspectorAirGapPath;
    private final Path nugetInspectorAirGapPath;
    private final Path gradleInspectorAirGapPath;

    public AirGapInspectorPaths(final AirGapPathFinder pathFinder, final AirGapOptions airGapOptions) {
        final File detectJar = pathFinder.findDetectJar();
        dockerInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getDockerInspectorPathOverride().orElse(null), AirGapPathFinder.DOCKER);
        gradleInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getGradleInspectorPathOverride().orElse(null), AirGapPathFinder.GRADLE);
        nugetInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getNugetInspectorPathOverride().orElse(null), AirGapPathFinder.NUGET);
    }

    private Path determineInspectorAirGapPath(final File detectJar, final AirGapPathFinder airGapPathFinder, final Path inspectorLocationProperty, final String inspectorName) {
        if (inspectorLocationProperty == null && detectJar != null) {
            try {
                return airGapPathFinder.createRelativePackagedInspectorsFile(detectJar.getParentFile(), inspectorName).toPath();
            } catch (final Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for %s, returning the detect property instead", inspectorName));
                logger.debug(e.getMessage());
            }
        }
        return inspectorLocationProperty;
    }

    public Optional<Path> getDockerInspectorAirGapPath() {
        return Optional.ofNullable(dockerInspectorAirGapPath);
    }

    private Optional<Path> getNugetInspectorAirGapPath() {
        return Optional.ofNullable(nugetInspectorAirGapPath);
    }

    private Optional<Path> getGradleInspectorAirGapPath() {
        return Optional.ofNullable(gradleInspectorAirGapPath);
    }

    public Optional<File> getNugetInspectorAirGapFile() {
        return getNugetInspectorAirGapPath().map(Path::toFile).filter(File::exists);
    }

    public Optional<File> getDockerInspectorAirGapFile() {
        return getDockerInspectorAirGapPath().map(Path::toFile).filter(File::exists);
    }

    public Optional<File> getGradleInspectorAirGapFile() {
        return getGradleInspectorAirGapPath().map(Path::toFile).filter(File::exists);
    }
}
