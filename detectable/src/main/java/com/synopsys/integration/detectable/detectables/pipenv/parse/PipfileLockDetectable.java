package com.synopsys.integration.detectable.detectables.pipenv.parse;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PipfileLockNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Pipfile Lock", language = "Python", forge = "PyPi", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: Pipfile, Pipfile.lock")
public class PipfileLockDetectable extends Detectable {
    private static final String PIPFILE_FILENAME = "Pipfile";
    private static final String PIPFILE_LOCK_FILENAME = "Pipfile.lock";

    private final FileFinder fileFinder;
    private final PipfileLockExtractor pipenvLockExtractor;

    private File pipfileLock;

    public PipfileLockDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        PipfileLockExtractor pipenvLockExtractor
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pipenvLockExtractor = pipenvLockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.eitherFile(
            PIPFILE_FILENAME,
            PIPFILE_LOCK_FILENAME,
            pipfile -> {},
            pipfileLock -> this.pipfileLock = pipfileLock
        );
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (pipfileLock == null) {
            return new PipfileLockNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return pipenvLockExtractor.extract(pipfileLock);
    }
}
