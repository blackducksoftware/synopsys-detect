package com.synopsys.integration.detectable.detectables.yarn;

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

@DetectableInfo(name = "Yarn Lock", language = "Node JS", forge = "npmjs", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: yarn.lock and package.json.")
public class YarnLockDetectable extends Detectable {
    public static final String YARN_LOCK_FILENAME = "yarn.lock";
    public static final String YARN_PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final YarnLockExtractor yarnLockExtractor;

    private File yarnLock;
    private File packageJson;

    public YarnLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, YarnLockExtractor yarnLockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        yarnLock = requirements.file(YARN_LOCK_FILENAME);
        packageJson = requirements.file(YARN_PACKAGE_JSON);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return yarnLockExtractor.extract(environment.getDirectory(), yarnLock, packageJson);
    }
}
