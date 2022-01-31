package com.synopsys.integration.detectable.detectables.go.gogradle;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;

public class GoGradleExtractor {
    private final GoGradleLockParser goGradleLockParser;

    public GoGradleExtractor(GoGradleLockParser goGradleLockParser) {
        this.goGradleLockParser = goGradleLockParser;
    }

    public Extraction extract(File goGradleLockFile) {
        try {
            DependencyGraph dependencyGraph = goGradleLockParser.parse(goGradleLockFile);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        } catch (IOException | IntegrationException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
