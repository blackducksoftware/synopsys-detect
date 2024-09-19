package com.blackduck.integration.detectable.detectables.cocoapods;

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

@DetectableInfo(name = "Pod Lock", language = "Objective C", forge = "COCOAPODS and NPMJS", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: Podfile.lock")
public class PodlockDetectable extends Detectable {
    private static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    private final FileFinder fileFinder;
    private final PodlockExtractor podlockExtractor;

    private File foundPodlock;

    public PodlockDetectable(DetectableEnvironment environment, FileFinder fileFinder, PodlockExtractor podlockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.podlockExtractor = podlockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        foundPodlock = requirements.file(PODFILE_LOCK_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return podlockExtractor.extract(foundPodlock);
    }

}
