/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirGapInspectorPaths {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Path dockerInspectorAirGapPath;
    private final Path nugetInspectorAirGapPath;
    private final Path gradleInspectorAirGapPath;
    @Nullable
    private final Path fontsAirGapPath;

    public AirGapInspectorPaths(AirGapPathFinder pathFinder, AirGapOptions airGapOptions) {
        File detectJar = pathFinder.findDetectJar();
        dockerInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getDockerInspectorPathOverride().orElse(null), AirGapPathFinder.DOCKER);
        gradleInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getGradleInspectorPathOverride().orElse(null), AirGapPathFinder.GRADLE);
        nugetInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, airGapOptions.getNugetInspectorPathOverride().orElse(null), AirGapPathFinder.NUGET);
        fontsAirGapPath = determineFontsAirGapPath(detectJar, pathFinder);
    }

    private Path determineInspectorAirGapPath(File detectJar, AirGapPathFinder airGapPathFinder, Path inspectorLocationProperty, String inspectorName) {
        if (inspectorLocationProperty == null && detectJar != null) {
            try {
                return airGapPathFinder.createRelativePackagedInspectorsFile(detectJar.getParentFile(), inspectorName).toPath();
            } catch (Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for %s, returning the detect property instead", inspectorName));
                logger.debug(e.getMessage());
            }
        }
        return inspectorLocationProperty;
    }

    private Path determineFontsAirGapPath(File detectJar, AirGapPathFinder airGapPathFinder) {
        if (detectJar != null) {
            try {
                return airGapPathFinder.createRelativeFontsFile(detectJar.getParentFile()).toPath();
            } catch (Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for fonts, returning the detect property instead"));
                logger.debug(e.getMessage());
            }
        }
        return null;
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

    public Optional<Path> getFontsAirGapPath() {
        return Optional.ofNullable(fontsAirGapPath);
    }

    public Optional<File> getFontsAirGapDirectory() {
        return getFontsAirGapPath().map(Path::toFile).filter(File::exists);
    }
}
