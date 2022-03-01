package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BitbakeGraph {
    private final List<BitbakeNode> nodes = new ArrayList<>();

    private BitbakeNode getOrCreate(String name) {
        Optional<BitbakeNode> existingNode = nodes.stream()
            .filter(node -> node.getName().equals(name))
            .findFirst();

        if (existingNode.isPresent()) {
            return existingNode.get();
        }

        BitbakeNode newNode = new BitbakeNode(name);
        nodes.add(newNode);
        return newNode;
    }

    public void addNode(String name, @Nullable String version, @Nullable String layer) {
        BitbakeNode node = getOrCreate(name);
        node.setVersion(version);
        node.setLayer(layer);
    }

    public void addChild(String parent, String child) {
        getOrCreate(parent).addChild(child);
    }

    public List<BitbakeNode> getNodes() {
        return nodes;
    }
}
