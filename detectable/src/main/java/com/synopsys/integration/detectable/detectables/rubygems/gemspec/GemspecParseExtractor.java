package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class GemspecParseExtractor {
    private final GemspecParser gemspecParser;

    public GemspecParseExtractor(GemspecParser gemspecParser) {
        this.gemspecParser = gemspecParser;
    }

    public Extraction extract(File gemspec) {
        try (InputStream inputStream = new FileInputStream(gemspec)) {
            DependencyGraph dependencyGraph = gemspecParser.parse(inputStream);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(codeLocation).build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
