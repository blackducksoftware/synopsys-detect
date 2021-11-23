/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Golang", forge = "Go Modules", requirementsMarkdown = "File: go.mod. Executable: go.")
public class GoModCliDetectable extends Detectable {
    public static final String GOMOD_FILENAME_PATTERN = "go.mod";

    private final FileFinder fileFinder;
    private final GoResolver goResolver;
    private final GoModCliExtractor goModCliExtractor;
    private final GoModCliDetectableOptions goModCliDetectableOptions;

    private ExecutableTarget goExe;

    public GoModCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoResolver goResolver, GoModCliExtractor goModCliExtractor, GoModCliDetectableOptions goModCliDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goResolver = goResolver;
        this.goModCliExtractor = goModCliExtractor;
        this.goModCliDetectableOptions = goModCliDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(GOMOD_FILENAME_PATTERN);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        goExe = requirements.executable(goResolver::resolveGo, "go");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return goModCliExtractor.extract(environment.getDirectory(), goExe, goModCliDetectableOptions.isDependencyVerificationEnabled());
    }
}
