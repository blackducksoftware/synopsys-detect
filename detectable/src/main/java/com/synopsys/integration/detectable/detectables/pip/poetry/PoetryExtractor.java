package com.synopsys.integration.detectable.detectables.pip.poetry;

import java.io.IOException;
import java.nio.file.Path;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pip.poetry.parser.PoetryLockParser;

public class PoetryExtractor {

    private final PoetryLockParser poetryLockParser;

    public PoetryExtractor(final PoetryLockParser poetryLockParser) {
        this.poetryLockParser = poetryLockParser;
    }

    public Extraction extract(Path path) throws IOException {
        final DependencyGraph graph = poetryLockParser.parseLockFile(path);
        final CodeLocation codeLocation = new CodeLocation(graph);
        return new Extraction.Builder().success(codeLocation).build();
    }
}
