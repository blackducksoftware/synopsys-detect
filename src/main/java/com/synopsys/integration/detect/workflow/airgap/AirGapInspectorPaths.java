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

    @Nullable
    private final Path dockerInspectorAirGapPath;
    @Nullable
    private final Path nugetInspectorAirGapPath;
    @Nullable
    private final Path gradleInspectorAirGapPath;
    @Nullable
    private final Path fontsAirGapPath;

    public AirGapInspectorPaths(AirGapPathFinder pathFinder) {
        File detectJar = pathFinder.findDetectJar();
        dockerInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, AirGapPathFinder.DOCKER);
        gradleInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, AirGapPathFinder.GRADLE);
        nugetInspectorAirGapPath = determineInspectorAirGapPath(detectJar, pathFinder, AirGapPathFinder.NUGET);
        fontsAirGapPath = determineFontsAirGapPath(detectJar, pathFinder);
    }

    private Path determineInspectorAirGapPath(File detectJar, AirGapPathFinder airGapPathFinder, String inspectorName) {
        try {
            return airGapPathFinder.createRelativePackagedInspectorsFile(detectJar.getParentFile(), inspectorName).toPath();
        } catch (Exception e) {
            logger.debug(String.format("Exception encountered when guessing air gap path for %s", inspectorName));
            logger.debug(e.getMessage());
            return null;
        }
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
