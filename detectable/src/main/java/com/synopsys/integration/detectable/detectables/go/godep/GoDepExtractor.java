/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.godep;

import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.godep.parse.GoLockParser;

public class GoDepExtractor {
    private final GoLockParser goLockParser;

    public GoDepExtractor(final GoLockParser goLockParser) {
        this.goLockParser = goLockParser;
    }

    public Extraction extract(final InputStream goLockInputStream) {
        final DependencyGraph graph = goLockParser.parseDepLock(goLockInputStream);
        final CodeLocation codeLocation = new CodeLocation(graph);
        return new Extraction.Builder().success(codeLocation).build();
    }

}
