package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

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
