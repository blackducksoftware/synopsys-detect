package com.blackduck.integration.detectable.detectables.rubygems.gemlock;

import java.io.File;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Gemfile Lock", language = "Ruby", forge = "RubyGems", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: Gemfile.lock.")
public class GemlockDetectable extends Detectable {
    private static final String GEMFILE_LOCK_FILENAME = "Gemfile.lock";

    private final FileFinder fileFinder;
    private final GemlockExtractor gemlockExtractor;

    private File gemlock;

    public GemlockDetectable(DetectableEnvironment environment, FileFinder fileFinder, GemlockExtractor gemlockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gemlockExtractor = gemlockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        gemlock = requirements.file(GEMFILE_LOCK_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return gemlockExtractor.extract(gemlock);
    }

}
