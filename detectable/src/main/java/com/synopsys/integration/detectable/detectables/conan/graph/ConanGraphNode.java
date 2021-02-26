/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.graph;

import java.util.ArrayList;
import java.util.List;

public class ConanGraphNode {
    private final ConanNode<String> conanNode;
    private final List<ConanGraphNode> children = new ArrayList<>();

    public ConanGraphNode(ConanNode<String> node) {
        this.conanNode = node;
    }

    public void addChild(ConanGraphNode node) {
        children.add(node);
    }

    public ConanNode<String> getConanNode() {
        return conanNode;
    }

    public List<ConanGraphNode> getChildren() {
        return children;
    }
}
