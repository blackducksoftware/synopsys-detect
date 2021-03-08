/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gogradle;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Golang", forge = "GitHub", requirementsMarkdown = "File: gogradle.lock.")
public class GoGradleDetectable extends Detectable {
    public static final String GO_GRADLE_LOCK = "gogradle.lock";

    private final FileFinder fileFinder;
    private final GoGradleExtractor goGradleExtractor;

    private File goLock;

    public GoGradleDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GoGradleExtractor goGradleExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goGradleExtractor = goGradleExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        goLock = requirements.file(GO_GRADLE_LOCK);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return goGradleExtractor.extract(goLock);
    }
}
