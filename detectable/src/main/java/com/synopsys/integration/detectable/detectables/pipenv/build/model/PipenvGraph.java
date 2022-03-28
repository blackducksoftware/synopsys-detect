package com.synopsys.integration.detectable.detectables.pipenv.build.model;

import java.util.List;

public class PipenvGraph {
    private final List<PipenvGraphEntry> entries;

    public PipenvGraph(List<PipenvGraphEntry> entries) {
        this.entries = entries;
    }

    public List<PipenvGraphEntry> getEntries() {
        return entries;
    }
}
