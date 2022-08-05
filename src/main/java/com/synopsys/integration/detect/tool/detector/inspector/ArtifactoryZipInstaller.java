package com.synopsys.integration.detect.tool.detector.inspector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.exception.IntegrationException;

public class ArtifactoryZipInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtifactResolver artifactResolver;

    public ArtifactoryZipInstaller(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;

    }

    @NotNull
    public File installZipFromSource(File dest, String ext, String base, String repository, String property) throws IntegrationException, IOException {
        return installZipFromSource(dest, ext, base, repository, property, null, null);
    }

    @NotNull
    public File installZipFromSource(
        File dest,
        String ext,
        String base,
        String repository,
        String property,
        @Nullable String overrideVersion,
        @Nullable String overrideArtifactPattern
    ) throws IntegrationException, IOException {
        String source = artifactResolver.resolveArtifactLocation(base, repository, property, overrideVersion, overrideArtifactPattern);
        return installZipFromSource(dest, ext, source);
    }

    @NotNull
    public File installZipFromSource(File dest, String ext, String source) throws IntegrationException {
        logger.debug("Resolved inspector url: " + source);
        String zipName = artifactResolver.parseFileName(source);
        logger.debug("Parsed artifact name: " + zipName);
        String inspectorFolderName = zipName.replace(ext, "");
        File inspectorFolder = new File(dest, inspectorFolderName);
        if (!inspectorFolder.exists()) {
            logger.debug("Downloading inspector.");
            File zipFile = new File(dest, zipName);
            try {
                artifactResolver.downloadArtifact(zipFile, source);
            } catch (IOException e) {
                logger.warn("Failed to download artifact: " + source);
                throw new IntegrationException("Failed to download artifact: " + source, e);
            }
            logger.debug("Extracting inspector.");
            try {
                DetectZipUtil.unzip(zipFile, inspectorFolder, Charset.defaultCharset());
            } catch (IOException e) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Exception extracting:", e);
                } else {
                    logger.warn("Failed to unzip artifact: " + zipFile);
                }
                throw new IntegrationException("Failed to unzip artifact: " + zipFile, e);
            }
            logger.debug("Unzipped, deleting downloaded zip.");
            FileUtils.deleteQuietly(zipFile);
        } else {
            logger.debug("Inspector is already downloaded, folder exists.");
        }
        return inspectorFolder;
    }
}
