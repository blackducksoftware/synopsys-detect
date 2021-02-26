/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cran;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "R", forge = "CRAN", requirementsMarkdown = "File: packrat.lock.")
public class PackratLockDetectable extends Detectable {
    public static final String PACKRATLOCK_FILE_NAME = "packrat.lock";

    private final FileFinder fileFinder;
    private final PackratLockExtractor packratLockExtractor;

    private File packratLockFile;

    public PackratLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PackratLockExtractor packratLockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packratLockExtractor = packratLockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packratLockFile = requirements.file(PACKRATLOCK_FILE_NAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return packratLockExtractor.extract(environment.getDirectory(), packratLockFile);
    }

}
