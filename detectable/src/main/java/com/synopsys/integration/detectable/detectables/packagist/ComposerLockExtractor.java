/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.packagist;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;

public class ComposerLockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PackagistParser packagistParser;

    public ComposerLockExtractor(final PackagistParser packagistParser) {
        this.packagistParser = packagistParser;
    }

    public Extraction extract(final File composerJson, final File composerLock, boolean includeDevDependencies) {
        try {
            final String composerJsonText = FileUtils.readFileToString(composerJson, StandardCharsets.UTF_8);
            final String composerLockText = FileUtils.readFileToString(composerLock, StandardCharsets.UTF_8);

            logger.debug(composerJsonText);
            logger.debug(composerLockText);

            final PackagistParseResult result = packagistParser.getDependencyGraphFromProject(composerJsonText, composerLockText, includeDevDependencies);

            return new Extraction.Builder()
                       .success(result.getCodeLocation())
                       .projectName(result.getProjectName())
                       .projectVersion(result.getProjectVersion())
                       .build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
