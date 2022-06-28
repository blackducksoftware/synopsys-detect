package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.GivenFileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Conan Lock", language = "C/C++", forge = "conan", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: conan.lock.")
public class ConanLockfileDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String CONANLOCKFILE = "conan.lock";
    private final FileFinder fileFinder;
    private final ConanLockfileExtractor conanLockfileExtractor;
    private final ConanLockfileExtractorOptions conanLockfileExtractorOptions;
    private File lockfile;

    public ConanLockfileDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        ConanLockfileExtractor conanLockfileExtractor,
        ConanLockfileExtractorOptions conanLockfileExtractorOptions
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanLockfileExtractor = conanLockfileExtractor;
        this.conanLockfileExtractorOptions = conanLockfileExtractorOptions;
    }

    @Override
    public DetectableResult applicable() {
        if (conanLockfileExtractorOptions.getLockfilePath().isPresent()) {
            Path conanLockFile = conanLockfileExtractorOptions.getLockfilePath().get();
            logger.debug("Conan Lockfile detectable applies because user supplied lockfile path {}", conanLockFile);
            return new PassedDetectableResult(new FoundFile(conanLockFile.toFile())); //TODO: Should lock file be reported as a relevant file?
        }
        Requirements requirements = new Requirements(fileFinder, environment);
        lockfile = requirements.file(CONANLOCKFILE);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (conanLockfileExtractorOptions.getLockfilePath().isPresent()) {
            Path givenLockfilePath = conanLockfileExtractorOptions.getLockfilePath().get();
            File userProvidedLockfile = givenLockfilePath.toFile();
            if (userProvidedLockfile.exists()) {
                lockfile = userProvidedLockfile;
            } else {
                logger.debug("File {} does not exist", givenLockfilePath);
                return new GivenFileNotFoundDetectableResult(givenLockfilePath.toString());
            }
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return conanLockfileExtractor.extract(lockfile);
    }
}
