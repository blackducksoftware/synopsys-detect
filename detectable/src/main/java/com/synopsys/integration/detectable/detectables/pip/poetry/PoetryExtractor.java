package com.synopsys.integration.detectable.detectables.pip.poetry;

import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pip.poetry.parser.PoetryLockParser;

public class PoetryExtractor {

    private final PoetryLockParser poetryLockParser;

    public PoetryExtractor(final PoetryLockParser poetryLockParser) {
        this.poetryLockParser = poetryLockParser;
    }

    public Extraction extract(InputStream poetryInputStream) {
        final DependencyGraph graph = poetryLockParser.parseLockFile(poetryInputStream);
        final CodeLocation codeLocation = new CodeLocation(graph);
        return new Extraction.Builder().success(codeLocation).build();
    }
}
