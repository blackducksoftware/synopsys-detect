/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors;

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
    public File installZipFromSource(final File dest, String ext, String base, String repository, String property) throws IntegrationException, IOException {
        return installZipFromSource(dest, ext, base, repository, property, null, null);
    }

    @NotNull
    public File installZipFromSource(final File dest, String ext, String base, String repository, String property, @Nullable String overrideVersion, @Nullable String overrideArtifactPattern) throws IntegrationException, IOException {
        String source = artifactResolver.resolveArtifactLocation(base, repository, property, overrideVersion, overrideArtifactPattern);
        return installZipFromSource(dest, ext, source);
    }

    @NotNull
    public File installZipFromSource(final File dest, String ext, final String source) throws IntegrationException {
        logger.debug("Resolved inspector url: " + source);
        final String zipName = artifactResolver.parseFileName(source);
        logger.debug("Parsed artifact name: " + zipName);
        final String inspectorFolderName = zipName.replace(ext, "");
        final File inspectorFolder = new File(dest, inspectorFolderName);
        if (!inspectorFolder.exists()) {
            logger.debug("Downloading inspector.");
            final File zipFile = new File(dest, zipName);
            try {
                artifactResolver.downloadArtifact(zipFile, source);
            } catch (IOException e) {
                throw new IntegrationException("Failed to download artifact: " + source, e);
            }
            logger.debug("Extracting inspector.");
            try {
                DetectZipUtil.unzip(zipFile, inspectorFolder, Charset.defaultCharset());
            } catch (IOException e) {
                logger.trace("Exception extracting:", e);
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
