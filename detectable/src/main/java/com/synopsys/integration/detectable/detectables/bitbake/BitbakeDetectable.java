/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.explanation.PropertyProvided;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

import java.io.File;

@DetectableInfo(language = "various", forge = "YOCTO", requirementsMarkdown = "Properties: Package names. File: build env script. Executable: bash")
public class BitbakeDetectable extends Detectable {
    private final BitbakeDetectableOptions bitbakeDetectableOptions;
    private final FileFinder fileFinder;
    private final BitbakeExtractor bitbakeExtractor;
    private final BashResolver bashResolver;

    private File foundBuildEnvScript;
    private ExecutableTarget bashExe;

    public BitbakeDetectable(DetectableEnvironment detectableEnvironment, FileFinder fileFinder, BitbakeDetectableOptions bitbakeDetectableOptions, BitbakeExtractor bitbakeExtractor,
        BashResolver bashResolver) {
        super(detectableEnvironment);
        this.fileFinder = fileFinder;
        this.bitbakeDetectableOptions = bitbakeDetectableOptions;
        this.bitbakeExtractor = bitbakeExtractor;
        this.bashResolver = bashResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        foundBuildEnvScript = requirements.file(bitbakeDetectableOptions.getBuildEnvName());

        if (bitbakeDetectableOptions.getPackageNames() == null || bitbakeDetectableOptions.getPackageNames().isEmpty()) {
            return new PropertyInsufficientDetectableResult("Bitbake requires that at least one package name is provided.");
        } else {
            requirements.explain(new PropertyProvided("Bitbake Package Names"));
        }

        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        bashExe = requirements.executable(bashResolver::resolveBash, "bash");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return bitbakeExtractor.extract(environment.getDirectory(), foundBuildEnvScript, bitbakeDetectableOptions.getSourceArguments(), bitbakeDetectableOptions.getPackageNames(),
            bitbakeDetectableOptions.isFollowSymLinks(), bitbakeDetectableOptions.getSearchDepth(), bashExe);
    }
}
