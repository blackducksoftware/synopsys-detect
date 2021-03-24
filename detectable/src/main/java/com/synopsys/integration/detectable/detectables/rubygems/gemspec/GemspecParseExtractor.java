/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecParser;

public class GemspecParseExtractor {
    private final GemspecParser gemspecParser;

    public GemspecParseExtractor(final GemspecParser gemspecParser) {
        this.gemspecParser = gemspecParser;
    }

    public Extraction extract(final File gemspec, final boolean includeRuntime, final boolean includeDev) {
        try (final InputStream inputStream = new FileInputStream(gemspec)) {
            final DependencyGraph dependencyGraph = gemspecParser.parse(inputStream, includeRuntime, includeDev);
            final CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(codeLocation).build();
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
