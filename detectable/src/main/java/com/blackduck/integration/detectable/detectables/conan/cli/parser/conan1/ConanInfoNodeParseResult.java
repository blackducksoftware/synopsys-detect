package com.blackduck.integration.detectable.detectables.conan.cli.parser.conan1;

import java.util.Optional;

import com.blackduck.integration.detectable.detectables.conan.graph.ConanNode;

public class ConanInfoNodeParseResult {
    private final int lastParsedLineIndex;
    private final ConanNode<String> conanNode;

    public ConanInfoNodeParseResult(int lastParsedLineIndex) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanNode = null;
    }

    public ConanInfoNodeParseResult(int lastParsedLineIndex, ConanNode<String> conanGraphNode) {
        this.lastParsedLineIndex = lastParsedLineIndex;
        this.conanNode = conanGraphNode;
    }

    public int getLastParsedLineIndex() {
        return lastParsedLineIndex;
    }

    public Optional<ConanNode<String>> getConanNode() {
        return Optional.ofNullable(conanNode);
    }
}
