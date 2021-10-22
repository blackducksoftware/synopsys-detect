/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "Files: pnpm-lock.yaml and package.json.")
public class PnpmDetectable extends Detectable {
    public static final String PNPM_LOCK_YAML_FILENAME = "pnpm-lock.yaml";
    public static final String PACKAGE_JSON = "package.json";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final PnpmExtractor pnpmExtractor;
    private final PnpmDetectableOptions pnpmDetectableOptions;

    private File pnpmLockYaml;
    private File packageJson;

    public PnpmDetectable(DetectableEnvironment environment, FileFinder fileFinder, PnpmExtractor pnpmExtractor,
        PnpmDetectableOptions pnpmDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pnpmExtractor = pnpmExtractor;
        this.pnpmDetectableOptions = pnpmDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        pnpmLockYaml = requirements.file(PNPM_LOCK_YAML_FILENAME);
        packageJson = requirements.optionalFile(PACKAGE_JSON, () -> logger.warn("Pnpm applied but it could not find a package.json so project name and version may not be determined."));
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return pnpmExtractor.extract(pnpmLockYaml, packageJson, pnpmDetectableOptions.shouldIncludeDevDependencies(), pnpmDetectableOptions.shouldIncludeOptionalDependencies());
    }
}
