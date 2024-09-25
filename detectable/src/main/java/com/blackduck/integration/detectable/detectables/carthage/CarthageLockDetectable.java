package com.blackduck.integration.detectable.detectables.carthage;

import java.io.File;
import java.io.IOException;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.result.CartfileResolvedNotFoundDetectableResult;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Carthage Lock", language = "various", forge = "GitHub", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: Cartfile, Cartfile.resolved")
public class CarthageLockDetectable extends Detectable {
    private static final String CARTFILE_FILENAME = "Cartfile";
    private static final String CARTFILE_RESOLVED_FILENAME = "Cartfile.resolved";

    private final FileFinder fileFinder;
    private final CarthageExtractor carthageExtractor;

    private File cartfileResolved;

    public CarthageLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, CarthageExtractor carthageExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.carthageExtractor = carthageExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.eitherFile(
            CARTFILE_FILENAME,
            CARTFILE_RESOLVED_FILENAME,
            foundCartfile -> {}, // Used only to identify Carthage projects
            foundCartfileResolved -> cartfileResolved = foundCartfileResolved
        );
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (cartfileResolved == null) {
            return new CartfileResolvedNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return carthageExtractor.extract(cartfileResolved);
    }
}
