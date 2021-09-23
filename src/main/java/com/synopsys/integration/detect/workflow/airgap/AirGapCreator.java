/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.result.AirGapDetectResult;

public class AirGapCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AirGapPathFinder airGapPathFinder;
    private final EventSystem eventSystem;
    private final GradleAirGapCreator gradleAirGapCreator;
    private final NugetAirGapCreator nugetAirGapCreator;
    private final DockerAirGapCreator dockerAirGapCreator;
    private final DetectFontAirGapCreator detectFontAirGapCreator;
    private final ProjectInspectorAirGapCreator projectInspectorAirGapCreator;

    public AirGapCreator(AirGapPathFinder airGapPathFinder, EventSystem eventSystem, GradleAirGapCreator gradleAirGapCreator, NugetAirGapCreator nugetAirGapCreator, DockerAirGapCreator dockerAirGapCreator,
        DetectFontAirGapCreator detectFontAirGapCreator, ProjectInspectorAirGapCreator projectInspectorAirGapCreator) {
        this.airGapPathFinder = airGapPathFinder;
        this.eventSystem = eventSystem;
        this.gradleAirGapCreator = gradleAirGapCreator;
        this.nugetAirGapCreator = nugetAirGapCreator;
        this.dockerAirGapCreator = dockerAirGapCreator;
        this.detectFontAirGapCreator = detectFontAirGapCreator;
        this.projectInspectorAirGapCreator = projectInspectorAirGapCreator;
    }

    public File createAirGapZip(AirGapType airGapType, File outputPath, String gradleInspectorVersion) throws DetectUserFriendlyException {
        try {
            logger.info("");
            logger.info(ReportConstants.RUN_SEPARATOR);
            logger.info(ReportConstants.RUN_SEPARATOR);
            logger.info("Detect is in Air Gap Creation mode.");
            logger.info("Detect will create an air gap of itself and then exit.");
            logger.info("The created air gap zip will not be cleaned up.");
            logger.info(String.format("Specify desired inspectors after -z argument in a comma separated list of ALL, NONE, %s", Arrays.stream(AirGapType.values()).map(Enum::name).collect(Collectors.joining(", "))));
            logger.info(ReportConstants.RUN_SEPARATOR);
            logger.info(ReportConstants.RUN_SEPARATOR);
            logger.info("");

            File detectJar = airGapPathFinder.findDetectJar();
            if (detectJar == null) {
                throw new DetectUserFriendlyException("To create an air gap zip, Detect must be run from a jar and be able to find that jar. Detect was unable to find it's own jar.", ExitCodeType.FAILURE_CONFIGURATION);
            }
            logger.info("The detect jar location: " + detectJar.getCanonicalPath());

            logger.info("Creating zip at location: " + outputPath);

            String basename = FilenameUtils.removeExtension(detectJar.getName());
            String airGapName = basename + "-air-gap";
            if (airGapType == AirGapType.NO_DOCKER) {
                airGapName = airGapName + "-no-docker";
            }
            File target = new File(outputPath, airGapName + ".zip");
            File installFolder = new File(outputPath, basename);

            logger.info("Will build the zip in the following folder: " + installFolder.getCanonicalPath());

            logger.info("Installing dependencies.");
            installAllAirGapDependencies(airGapType, installFolder, gradleInspectorVersion);

            logger.info("Copying detect jar.");
            FileUtils.copyFile(detectJar, new File(installFolder, detectJar.getName()));

            logger.info("Zipping into: " + target.getCanonicalPath());
            ZipUtil.pack(installFolder, target);

            logger.info("Cleaning up working directory: " + installFolder.getCanonicalPath());
            FileUtils.deleteDirectory(installFolder);

            logger.info(ReportConstants.RUN_SEPARATOR);
            String result = target.getCanonicalPath();
            logger.info("Successfully created air gap zip: " + result);
            logger.info(ReportConstants.RUN_SEPARATOR);

            eventSystem.publishEvent(Event.ResultProduced, new AirGapDetectResult(result));
            return target;
        } catch (IOException e) {
            throw new DetectUserFriendlyException("Failed to create detect air gap zip.", e, ExitCodeType.FAILURE_UNKNOWN_ERROR);
        }
    }

    public void installAllAirGapDependencies(AirGapType airGapType, File zipFolder, String gradleInspectorVersion) throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);

        logger.info("Installing font dependencies.");
        File fontFolder = airGapPathFinder.createRelativeFontsFile(zipFolder);
        detectFontAirGapCreator.installFonts(fontFolder);

        logger.info(ReportConstants.RUN_SEPARATOR);

        logger.info("Installing gradle dependencies.");
        File gradleTemp = airGapPathFinder.createRelativePackagedInspectorsFile(zipFolder, AirGapPathFinder.GRADLE + "-temp");
        File gradleTarget = airGapPathFinder.createRelativePackagedInspectorsFile(zipFolder, AirGapPathFinder.GRADLE);
        gradleAirGapCreator.installGradleDependencies(gradleTemp, gradleTarget, gradleInspectorVersion);

        logger.info(ReportConstants.RUN_SEPARATOR);

        logger.info("Installing nuget dependencies.");
        File nugetFolder = airGapPathFinder.createRelativePackagedInspectorsFile(zipFolder, AirGapPathFinder.NUGET);
        nugetAirGapCreator.installNugetDependencies(nugetFolder);

        logger.info(ReportConstants.RUN_SEPARATOR);

        logger.info("Installing project inspector dependencies.");
        File projectFolder = airGapPathFinder.createRelativePackagedInspectorsFile(zipFolder, AirGapPathFinder.PROJECT_INSPECTOR);
        projectInspectorAirGapCreator.installDependencies(projectFolder);

        logger.info(ReportConstants.RUN_SEPARATOR);

        if (airGapType == AirGapType.FULL) {
            logger.info("Will include DOCKER inspector.");
            logger.info("Installing docker dependencies.");
            File dockerFolder = airGapPathFinder.createRelativePackagedInspectorsFile(zipFolder, AirGapPathFinder.DOCKER);
            dockerAirGapCreator.installDockerDependencies(dockerFolder);
        } else {
            logger.info("Will NOT include DOCKER inspector.");
        }
        logger.info(ReportConstants.RUN_SEPARATOR);
    }

}
