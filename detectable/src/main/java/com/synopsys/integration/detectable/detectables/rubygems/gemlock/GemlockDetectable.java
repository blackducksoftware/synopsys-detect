package com.synopsys.integration.detectable.detectables.rubygems.gemlock;

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
