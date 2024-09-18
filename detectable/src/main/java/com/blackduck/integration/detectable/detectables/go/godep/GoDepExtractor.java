package com.blackduck.integration.detectable.detectables.go.godep;

import java.io.InputStream;

import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectables.go.godep.parse.GoLockParser;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.synopsys.integration.bdio.graph.DependencyGraph;

public class GoDepExtractor {
    private final GoLockParser goLockParser;

    public GoDepExtractor(GoLockParser goLockParser) {
        this.goLockParser = goLockParser;
    }

    public Extraction extract(InputStream goLockInputStream) {
        DependencyGraph graph = goLockParser.parseDepLock(goLockInputStream);
        CodeLocation codeLocation = new CodeLocation(graph);
        return new Extraction.Builder().success(codeLocation).build();
    }

}
