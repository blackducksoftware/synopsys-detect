package com.synopsys.integration.detectable.detectables.go.godep;

import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.godep.parse.GoLockParser;
import com.synopsys.integration.detectable.extraction.Extraction;

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
