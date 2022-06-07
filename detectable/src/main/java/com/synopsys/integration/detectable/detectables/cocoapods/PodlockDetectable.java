package com.synopsys.integration.detectable.detectables.cocoapods;

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
