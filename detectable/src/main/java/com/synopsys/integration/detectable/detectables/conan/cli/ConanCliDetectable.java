/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "C/C++", forge = "conan", requirementsMarkdown = "Files: conanfile.txt or conanfile.py. <br /><br /> Executable: conan.")
public class ConanCliDetectable extends Detectable {
    public static final String CONANFILETXT = "conanfile.txt";
    public static final String CONANFILEPY = "conanfile.py";
    private final FileFinder fileFinder;
    private final ConanResolver conanResolver;
    private final ConanCliExtractor conanCliExtractor;
    private final ConanCliExtractorOptions conanCliExtractorOptions;

    private ExecutableTarget conanExe;

    public ConanCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanResolver conanResolver, ConanCliExtractor conanCliExtractor,
        ConanCliExtractorOptions conanCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanResolver = conanResolver;
        this.conanCliExtractor = conanCliExtractor;
        this.conanCliExtractorOptions = conanCliExtractorOptions;
    }

    @Override
    public DetectableResult applicable() {
        PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
        File conanTxtFile = fileFinder.findFile(environment.getDirectory(), CONANFILETXT);
        if (conanTxtFile == null) {
            File conanPyFile = fileFinder.findFile(environment.getDirectory(), CONANFILEPY);
            if (conanPyFile == null) {
                return new FileNotFoundDetectableResult(CONANFILETXT);
            } else {
                passedResultBuilder.foundFile(conanPyFile);
            }
        } else {
            passedResultBuilder.foundFile(conanTxtFile);
        }
        return passedResultBuilder.build();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        conanExe = requirements.executable(() -> conanResolver.resolveConan(environment), "conan");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return conanCliExtractor.extract(environment.getDirectory(), conanExe, conanCliExtractorOptions);
    }
}
