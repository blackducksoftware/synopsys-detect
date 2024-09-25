package com.blackduck.integration.detectable.detectables.rubygems.gemspec;

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

@DetectableInfo(name = "Gemspec Parse", language = "Ruby", forge = "RubyGems", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: A gemspec file (with .gemspec extension).")
public class GemspecParseDetectable extends Detectable {
    private static final String GEMSPEC_FILENAME = "*.gemspec";

    private final FileFinder fileFinder;
    private final GemspecParseExtractor gemspecParseExtractor;

    private File gemspec;

    public GemspecParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, GemspecParseExtractor gemspecParseExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gemspecParseExtractor = gemspecParseExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        gemspec = requirements.file(GEMSPEC_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return gemspecParseExtractor.extract(gemspec);
    }
}
