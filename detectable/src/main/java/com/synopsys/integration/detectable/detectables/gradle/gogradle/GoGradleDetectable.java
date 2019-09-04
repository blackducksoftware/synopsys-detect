package com.synopsys.integration.detectable.detectables.gradle.gogradle;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class GoGradleDetectable extends Detectable {
    private static final String GO_GRADLE_LOCK = "gogradle.lock";

    private final FileFinder fileFinder;
    private final GoGradleExtractor goGradleExtractor;

    private File goLock;

    public GoGradleDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GoGradleExtractor goGradleExtractor) {
        super(environment, "Go Gradle", "Go Gradle Lock");
        this.fileFinder = fileFinder;
        this.goGradleExtractor = goGradleExtractor;
    }

    @Override
    public DetectableResult applicable() {
        goLock = fileFinder.findFile(environment.getDirectory(), GO_GRADLE_LOCK);
        if (goLock == null) {
            return new FileNotFoundDetectableResult(GO_GRADLE_LOCK);
        }

        return new PassedDetectableResult();
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
