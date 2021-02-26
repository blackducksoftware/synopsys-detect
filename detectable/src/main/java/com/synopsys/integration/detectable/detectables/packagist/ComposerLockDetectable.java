/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.packagist;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "PHP", forge = "Packagist.org", requirementsMarkdown = "Files: composer.lock, composer.json.")
public class ComposerLockDetectable extends Detectable {
    private static final String COMPOSER_LOCK = "composer.lock";
    private static final String COMPOSER_JSON = "composer.json";

    private final FileFinder fileFinder;
    private final ComposerLockExtractor composerLockExtractor;
    private final ComposerLockDetectableOptions composerLockDetectableOptions;

    private File composerLock;
    private File composerJson;

    public ComposerLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final ComposerLockExtractor composerLockExtractor, ComposerLockDetectableOptions composerLockDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.composerLockExtractor = composerLockExtractor;
        this.composerLockDetectableOptions = composerLockDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        composerLock = requirements.file(COMPOSER_LOCK);
        composerJson = requirements.file(COMPOSER_JSON);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return composerLockExtractor.extract(composerJson, composerLock, composerLockDetectableOptions.shouldIncludeDevDependencies());
    }

}
