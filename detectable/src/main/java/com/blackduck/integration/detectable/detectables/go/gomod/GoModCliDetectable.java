package com.blackduck.integration.detectable.detectables.go.gomod;

import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;
import com.google.gson.JsonSyntaxException;
import com.blackduck.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;

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
