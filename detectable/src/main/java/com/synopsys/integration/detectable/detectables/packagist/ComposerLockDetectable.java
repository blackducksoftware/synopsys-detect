package com.synopsys.integration.detectable.detectables.packagist;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

// TODO: I'm confused by the interchangeability of 'composer' and 'packagist' in the names of classes here and tests. JM-01/2022
@DetectableInfo(name = "Composer Lock", language = "PHP", forge = "Packagist.org", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: composer.lock, composer.json.")
public class ComposerLockDetectable extends Detectable {
    private static final String COMPOSER_LOCK = "composer.lock";
    private static final String COMPOSER_JSON = "composer.json";

    private final FileFinder fileFinder;
    private final ComposerLockExtractor composerLockExtractor;

    private File composerLock;
    private File composerJson;

    public ComposerLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, ComposerLockExtractor composerLockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.composerLockExtractor = composerLockExtractor;
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return composerLockExtractor.extract(composerJson, composerLock);
    }

}
