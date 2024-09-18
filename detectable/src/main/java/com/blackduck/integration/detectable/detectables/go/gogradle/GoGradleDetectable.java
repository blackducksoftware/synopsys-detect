package com.blackduck.integration.detectable.detectables.go.gogradle;

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
