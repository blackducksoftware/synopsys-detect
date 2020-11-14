package com.synopsys.integration.detectable.detectables.conan.cli;

import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNode;

public class ConanInfoNodeParseResult {
    private final int lastParsedLineIndex;
    private final ConanGraphNode conanGraphNode;

    public ConanInfoNodeParseResult(int lastParsedLineIndex, ConanGraphNode conanGraphNode) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanGraphNode = conanGraphNode;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public ConanGraphNode getConanGraphNode() {
        return conanGraphNode;
    }
}
