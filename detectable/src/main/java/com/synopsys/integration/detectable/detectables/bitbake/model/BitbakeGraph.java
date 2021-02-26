/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BitbakeGraph {
    private final List<BitbakeNode> nodes = new ArrayList<>();

    private BitbakeNode getOrCreate(final String name) {
        final Optional<BitbakeNode> existingNode = nodes.stream()
                                                       .filter(node -> node.getName().equals(name))
                                                       .findFirst();

        if (existingNode.isPresent()) {
            return existingNode.get();
        }

        final BitbakeNode newNode = new BitbakeNode(name);
        nodes.add(newNode);
        return newNode;
    }

    public void addNode(final String name, @Nullable final String version) {
        getOrCreate(name).setVersion(version);
    }

    public void addChild(final String parent, final String child) {
        getOrCreate(parent).addChild(child);
    }

    public List<BitbakeNode> getNodes() {
        return nodes;
    }
}
