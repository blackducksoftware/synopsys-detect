package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.LernaResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class LernaDetectable extends Detectable {
    private static final String LERNA_JSON = "lerna.json";

    private final FileFinder fileFinder;
    private final LernaResolver lernaResolver;
    private final LernaExtractor lernaExtractor;

    private File lernaExecutable;

    public LernaDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final LernaResolver lernaResolver, final LernaExtractor lernaExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.lernaResolver = lernaResolver;
        this.lernaExtractor = lernaExtractor;
    }

    @Override
    public DetectableResult applicable() {
        final File lernaJsonFile = fileFinder.findFile(environment.getDirectory(), LERNA_JSON);

        if (lernaJsonFile == null) {
            return new FileNotFoundDetectableResult(LERNA_JSON);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        lernaExecutable = lernaResolver.resolveLerna();

        if (lernaExecutable == null) {
            return new ExecutableNotFoundDetectableResult("lerna");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return lernaExtractor.extract(lernaExecutable, extractionEnvironment);
    }
}
