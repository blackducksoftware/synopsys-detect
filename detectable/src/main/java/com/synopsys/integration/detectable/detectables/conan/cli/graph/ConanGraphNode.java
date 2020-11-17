package com.synopsys.integration.detectable.detectables.conan.cli.graph;

import java.util.ArrayList;
import java.util.List;

public class ConanGraphNode {
    private final ConanNode node;
    private final List<ConanGraphNode> children = new ArrayList<>();

    public ConanGraphNode(ConanNode node) {
        this.node = node;
    }

    public void addChild(ConanGraphNode node) {
        children.add(node);
    }

    public ConanNode getNode() {
        return node;
    }
}
