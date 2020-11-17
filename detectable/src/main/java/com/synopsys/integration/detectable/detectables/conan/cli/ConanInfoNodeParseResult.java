package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.Optional;

import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNode;

public class ConanInfoNodeParseResult {
    private final int lastParsedLineIndex;
    private final Optional<ConanGraphNode> conanGraphNode;

    public ConanInfoNodeParseResult(int lastParsedLineIndex) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanGraphNode = Optional.empty();
    }

    public ConanInfoNodeParseResult(int lastParsedLineIndex, Optional<ConanGraphNode> conanGraphNode) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanGraphNode = conanGraphNode;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public Optional<ConanGraphNode> getConanGraphNode() {
        return conanGraphNode;
    }
}
