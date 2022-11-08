package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.explanation.PropertyProvided;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Bitbake CLI", language = "various", forge = "YOCTO", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Properties: Package names. File: build env script. Executable: bash")
public class BitbakeDetectable extends Detectable {
    private final BitbakeDetectableOptions bitbakeDetectableOptions;
    private final FileFinder fileFinder;
    private final BitbakeExtractor bitbakeExtractor;
    private final BashResolver bashResolver;

    private File foundBuildEnvScript;
    private ExecutableTarget bashExe;

    public BitbakeDetectable(
        DetectableEnvironment detectableEnvironment,
        FileFinder fileFinder,
        BitbakeDetectableOptions bitbakeDetectableOptions,
        BitbakeExtractor bitbakeExtractor,
        BashResolver bashResolver
    ) {
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return bitbakeExtractor.extract(environment.getDirectory(), bashExe, foundBuildEnvScript);
    }
}
