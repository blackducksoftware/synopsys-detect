/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.ArtifactResolver;

public class DetectFontInstaller {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ArtifactResolver artifactResolver;

    public DetectFontInstaller(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    public File installFonts(File targetDirectory) {
        logger.debug("Installing fonts...");
        return null;
    }
}
