package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.Optional;

import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanNode;

public class ConanInfoNodeParseResult {
    private final int lastParsedLineIndex;
    private final Optional<ConanNode> conanGraphNode;

    public ConanInfoNodeParseResult(int lastParsedLineIndex) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanGraphNode = Optional.empty();
    }

    public ConanInfoNodeParseResult(int lastParsedLineIndex, Optional<ConanNode> conanGraphNode) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanGraphNode = conanGraphNode;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public Optional<ConanNode> getConanGraphNode() {
        return conanGraphNode;
    }
}
