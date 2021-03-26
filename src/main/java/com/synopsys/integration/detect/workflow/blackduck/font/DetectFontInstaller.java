/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.CommonZipExpander;

public class DetectFontInstaller {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ArtifactResolver artifactResolver;

    public DetectFontInstaller(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    public void installFonts(File targetDirectory) {
        try {
            logger.info("Determining the location of the fonts bundle.");
            String location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.FONTS_REPO, ArtifactoryConstants.FONTS_PROPERTY, "", "");
            File fontZipFile = downloadZipFile(location, targetDirectory);
            expandZipFile(fontZipFile, targetDirectory);
            String zipFilePath = fontZipFile.getAbsolutePath();
            logger.debug("Deleting zip file {}", zipFilePath);
            boolean zipDeleted = FileUtils.deleteQuietly(fontZipFile);
            if (zipDeleted) {
                logger.debug("Successfully deleted zip file {}", zipFilePath);
            }
        } catch (IOException | IntegrationException | ArchiveException ex) {
            logger.error(String.format("Failed to load font files into %s", targetDirectory.getAbsolutePath()));
            logger.error("Cause: ", ex);
        }
    }

    private File downloadZipFile(String location, File targetFontsDirectory) throws IOException, IntegrationException {
        logger.info("Finding or downloading the fonts zip file.");
        logger.debug(String.format("Downloading fonts file from from '%s' to '%s'.", location, targetFontsDirectory.getAbsolutePath()));
        File zipFile = artifactResolver.downloadOrFindArtifact(targetFontsDirectory, location);
        logger.info("Found online fonts bundle: {}", zipFile.getAbsolutePath());
        return zipFile;
    }

    private void expandZipFile(File fontZipFile, File targetDirectory) throws IntegrationException, ArchiveException, IOException {
        logger.info("Expanding the zip file {} to {}", fontZipFile.getAbsolutePath(), targetDirectory.getAbsolutePath());
        Slf4jIntLogger intLogger = new Slf4jIntLogger(logger);
        CommonZipExpander zipExpander = new CommonZipExpander(intLogger);
        zipExpander.expand(fontZipFile, targetDirectory);
    }
}
