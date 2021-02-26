/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.GivenFileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "C/C++", forge = "conan", requirementsMarkdown = "Files: conan.lock.")
public class ConanLockfileDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String CONANLOCKFILE = "conan.lock";
    private final FileFinder fileFinder;
    private final ConanLockfileExtractor conanLockfileExtractor;
    private final ConanLockfileExtractorOptions conanLockfileExtractorOptions;
    private File lockfile;

    public ConanLockfileDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanLockfileExtractor conanLockfileExtractor,
        ConanLockfileExtractorOptions conanLockfileExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanLockfileExtractor = conanLockfileExtractor;
        this.conanLockfileExtractorOptions = conanLockfileExtractorOptions;
    }

    @Override
    public DetectableResult applicable() {
        if (conanLockfileExtractorOptions.getLockfilePath().isPresent()) {
            String conanLockFile = conanLockfileExtractorOptions.getLockfilePath().get();
            logger.debug("Conan Lockfile detectable applies because user supplied lockfile path {}", conanLockFile);
            return new PassedDetectableResult(new FoundFile(conanLockFile)); //TODO: Should lock file be reported as a relevant file?
        }
        Requirements requirements = new Requirements(fileFinder, environment);
        lockfile = requirements.file(CONANLOCKFILE);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (conanLockfileExtractorOptions.getLockfilePath().isPresent()) {
            String givenLockfilePath = conanLockfileExtractorOptions.getLockfilePath().get();
            File userProvidedLockfile = new File(givenLockfilePath);
            if (userProvidedLockfile.exists()) {
                lockfile = userProvidedLockfile;
            } else {
                logger.debug("File {} does not exist", givenLockfilePath);
                return new GivenFileNotFoundDetectableResult(givenLockfilePath);
            }
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return conanLockfileExtractor.extract(lockfile, conanLockfileExtractorOptions);
    }
}
