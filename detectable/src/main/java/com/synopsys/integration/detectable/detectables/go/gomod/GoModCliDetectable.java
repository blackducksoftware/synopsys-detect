package com.synopsys.integration.detectable.detectables.go.gomod;

import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "GoMod CLI", language = "Golang", forge = "Go Modules", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: go.mod. Executable: go.")
public class GoModCliDetectable extends Detectable {
    public static final String GOMOD_FILENAME_PATTERN = "go.mod";

    private final FileFinder fileFinder;
    private final GoResolver goResolver;
    private final GoModCliExtractor goModCliExtractor;

    private ExecutableTarget goExe;

    public GoModCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoResolver goResolver, GoModCliExtractor goModCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goResolver = goResolver;
        this.goModCliExtractor = goModCliExtractor;
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, JsonSyntaxException, DetectableException {
        return goModCliExtractor.extract(environment.getDirectory(), goExe);
    }
}
