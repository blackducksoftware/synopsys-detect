package com.synopsys.integration.detectable.detectables.conda;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Conda CLI", language = "Python", forge = "Anaconda", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: environment.yml. Executable: conda.")
public class CondaCliDetectable extends Detectable {
    public static final String ENVIRONMENT_YML = "environment.yml";

    private final FileFinder fileFinder;
    private final CondaResolver condaResolver;
    private final CondaCliExtractor condaExtractor;
    private final CondaCliDetectableOptions condaCliDetectableOptions;

    private ExecutableTarget condaExe;

    public CondaCliDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        CondaResolver condaResolver,
        CondaCliExtractor condaExtractor,
        CondaCliDetectableOptions condaCliDetectableOptions
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.condaResolver = condaResolver;
        this.condaExtractor = condaExtractor;
        this.condaCliDetectableOptions = condaCliDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(ENVIRONMENT_YML);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        condaExe = requirements.executable(condaResolver::resolveConda, "conda");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return condaExtractor.extract(
            environment.getDirectory(),
            condaExe,
            extractionEnvironment.getOutputDirectory(),
            condaCliDetectableOptions.getCondaEnvironmentName().orElse("")
        );
    }

}
