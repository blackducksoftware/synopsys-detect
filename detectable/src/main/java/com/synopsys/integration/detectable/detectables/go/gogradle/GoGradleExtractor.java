/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gogradle;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.extraction.Extraction;
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
