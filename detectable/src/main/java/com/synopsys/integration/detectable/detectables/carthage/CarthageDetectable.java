/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.carthage;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.CartfileResolvedNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "GitHub", requirementsMarkdown = "Files: Cartfile, Cartfile.resolved")
public class CarthageDetectable extends Detectable {
    private static final String CARTFILE_FILENAME = "Cartfile";
    private static final String CARTFILE_RESOLVED_FILENAME = "Cartfile.resolved";

    private FileFinder fileFinder;
    private CarthageExtractor carthageExtractor;

    private File cartfile;
    private File cartfileResolved;

    public CarthageDetectable(DetectableEnvironment environment, FileFinder fileFinder, CarthageExtractor carthageExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.carthageExtractor = carthageExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);

        cartfile = fileFinder.findFile(environment.getDirectory(), CARTFILE_FILENAME);
        cartfileResolved = fileFinder.findFile(environment.getDirectory(), CARTFILE_RESOLVED_FILENAME);

        if (cartfile == null && cartfileResolved == null) {
            return new FilesNotFoundDetectableResult(CARTFILE_FILENAME, CARTFILE_RESOLVED_FILENAME);
        }
        if (cartfile != null) {
            requirements.explainFile(cartfile);
        }
        if (cartfileResolved != null) {
            requirements.explainFile(cartfileResolved);
        }
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        if (cartfileResolved == null && cartfile != null) {
            return new CartfileResolvedNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return carthageExtractor.extract(cartfileResolved);
    }
}
