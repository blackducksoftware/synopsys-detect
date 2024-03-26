package com.synopsys.integration.detectable.detectables.buildroot;

import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.MakeResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.executable.ExecutableRunnerException;

@DetectableInfo(name = "Buildroot", language = "various", forge = "Buildroot", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: .confg, Makefile. Executable: make.")
public class BuildrootDetectable extends Detectable {
    public static final String CONFIG_FILENAME   = ".config";
    public static final String MAKEFILE_FILENAME = "Makefile";

    private final FileFinder fileFinder;
    private final BuildrootExtractor buildrootExtractor;
    private final MakeResolver makeResolver;

    private ExecutableTarget makeExe;
    
    public BuildrootDetectable(DetectableEnvironment environment, FileFinder fileFinder, BuildrootExtractor buildrootExtractor, MakeResolver makeResolver) {
        super(environment);

        this.fileFinder = fileFinder;
        this.buildrootExtractor = buildrootExtractor;
        this.makeResolver = makeResolver;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(CONFIG_FILENAME);
        requirements.file(MAKEFILE_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        makeExe = requirements.executable(makeResolver::resolveMake, "make");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException, MissingExternalIdException, ExecutableFailedException {
        return buildrootExtractor.extract(makeExe, environment.getDirectory());
    }
}
