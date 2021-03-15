/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Scala", forge = "Maven Central", requirementsMarkdown = "File: build.sbt.")
public class SbtResolutionCacheDetectable extends Detectable {
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    private final FileFinder fileFinder;
    private final SbtResolutionCacheExtractor sbtResolutionCacheExtractor;
    private final SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions;

    public SbtResolutionCacheDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final SbtResolutionCacheExtractor sbtResolutionCacheExtractor,
        SbtResolutionCacheDetectableOptions sbtResolutionCacheDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.sbtResolutionCacheExtractor = sbtResolutionCacheExtractor;
        this.sbtResolutionCacheDetectableOptions = sbtResolutionCacheDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(BUILD_SBT_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        // TODO: This extractor probably is due for a re-write
        return sbtResolutionCacheExtractor.extract(environment.getDirectory(), sbtResolutionCacheDetectableOptions);
    }

}
