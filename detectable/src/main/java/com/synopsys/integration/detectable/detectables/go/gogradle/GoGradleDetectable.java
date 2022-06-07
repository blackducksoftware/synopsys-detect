package com.synopsys.integration.detectable.detectables.go.gogradle;

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

@DetectableInfo(name = "GoGradle Lock", language = "Golang", forge = "GitHub", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: gogradle.lock.")
public class GoGradleDetectable extends Detectable {
    public static final String GO_GRADLE_LOCK = "gogradle.lock";

    private final FileFinder fileFinder;
    private final GoGradleExtractor goGradleExtractor;

    private File goLock;

    public GoGradleDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoGradleExtractor goGradleExtractor) {
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return goGradleExtractor.extract(goLock);
    }
}
