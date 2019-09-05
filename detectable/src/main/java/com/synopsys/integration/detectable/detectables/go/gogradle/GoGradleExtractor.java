package com.synopsys.integration.detectable.detectables.go.gogradle;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.exception.IntegrationException;

public class GoGradleExtractor {
    private final GoGradleLockParser goGradleLockParser;

    public GoGradleExtractor(final GoGradleLockParser goGradleLockParser) {
        this.goGradleLockParser = goGradleLockParser;
    }

    public Extraction extract(final File goGradleLockFile) {
        try {
            final DependencyGraph dependencyGraph = goGradleLockParser.parse(goGradleLockFile);
            final CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        } catch (final IOException | IntegrationException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
