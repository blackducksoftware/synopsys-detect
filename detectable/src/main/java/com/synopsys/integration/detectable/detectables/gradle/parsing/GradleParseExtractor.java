/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;

public class GradleParseExtractor {
    private final BuildGradleParser buildGradleParser;

    public GradleParseExtractor(final BuildGradleParser buildGradleParser) {
        this.buildGradleParser = buildGradleParser;
    }

    public Extraction extract(final File buildFile) {
        try (final InputStream buildFileInputStream = new FileInputStream(buildFile)) {
            final Optional<DependencyGraph> dependencyGraph = buildGradleParser.parse(buildFileInputStream);

            if (dependencyGraph.isPresent()) {
                final CodeLocation codeLocation = new CodeLocation(dependencyGraph.get());
                return new Extraction.Builder().success(codeLocation).build();
            } else {
                return new Extraction.Builder().failure("Failed to extract dependencies.").build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
