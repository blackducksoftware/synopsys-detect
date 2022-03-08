package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.CommonZipExpander;

public class DetectFontInstaller {
    private static final String FONTS_ZIP_KEY = "fonts";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ArtifactResolver artifactResolver;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    public DetectFontInstaller(ArtifactResolver artifactResolver, InstalledToolManager installedToolManager, InstalledToolLocator installedToolLocator) {
        this.artifactResolver = artifactResolver;
        this.installedToolManager = installedToolManager;
        this.installedToolLocator = installedToolLocator;
    }

    public void installFonts(File targetDirectory) {
        try {
            logger.info("Determining the location of the fonts bundle.");
            String location = artifactResolver.resolveArtifactLocation(
                ArtifactoryConstants.ARTIFACTORY_URL,
                ArtifactoryConstants.FONTS_REPO,
                ArtifactoryConstants.FONTS_PROPERTY,
                "",
                ""
            );
            File fontZipFile = downloadZipFile(location, targetDirectory);
            expandZipFile(fontZipFile, targetDirectory);
            installedToolManager.saveInstalledToolLocation(FONTS_ZIP_KEY, targetDirectory.getAbsolutePath());

            String zipFilePath = fontZipFile.getAbsolutePath();
            logger.debug("Deleting zip file {}", zipFilePath);
            boolean zipDeleted = FileUtils.deleteQuietly(fontZipFile);
            if (zipDeleted) {
                logger.debug("Successfully deleted zip file {}", zipFilePath);
            }
        } catch (IOException | IntegrationException | ArchiveException onlineInstallException) {
            logger.error("Online install exception cause: ", onlineInstallException);
            logger.debug("Attempting to download fonts from previous install.");
            installedToolLocator.locateTool(FONTS_ZIP_KEY).ifPresent(fonts ->
            {
                try {
                    FileUtils.copyDirectory(fonts, targetDirectory);
                } catch (IOException localInstallException) {
                    logger.error(String.format("Failed to load font files into %s", targetDirectory.getAbsolutePath()));
                    logger.error("Local install exception: ", localInstallException);
                }
            });
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
