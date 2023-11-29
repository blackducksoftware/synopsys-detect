package com.synopsys.integration.detectable.detectables.conan.cli.parser.conan2.model;

import com.synopsys.integration.util.Stringable;

public class ConanGraphInfo extends Stringable {
    private final ConanGraphInfoGraph graph;

    public ConanGraphInfo(ConanGraphInfoGraph graph) {
        this.graph = graph;
    }

    public ConanGraphInfoGraph getGraph() {
        return graph;
    }
}
