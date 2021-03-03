/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

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

@DetectableInfo(language = "Ruby", forge = "RubyGems", requirementsMarkdown = "File: A gemspec file (with .gemspec extension).")
public class GemspecParseDetectable extends Detectable {
    private static final String GEMSPEC_FILENAME = "*.gemspec";

    private final FileFinder fileFinder;
    private final GemspecParseExtractor gemspecParseExtractor;
    private final GemspecParseDetectableOptions gemspecParseDetectableOptions;

    private File gemspec;

    public GemspecParseDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GemspecParseExtractor gemspecParseExtractor,
        final GemspecParseDetectableOptions gemspecParseDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gemspecParseExtractor = gemspecParseExtractor;
        this.gemspecParseDetectableOptions = gemspecParseDetectableOptions;
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
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return gemspecParseExtractor.extract(gemspec, gemspecParseDetectableOptions.shouldIncludeRuntimeDependencies(), gemspecParseDetectableOptions.shouldIncludeDevelopmentDependencies());
    }
}
