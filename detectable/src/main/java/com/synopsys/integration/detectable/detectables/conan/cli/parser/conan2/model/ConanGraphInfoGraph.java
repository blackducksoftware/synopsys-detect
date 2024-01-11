package com.synopsys.integration.detectable.detectables.conan.cli.parser.conan2.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ConanGraphInfoGraph extends Stringable {
    @SerializedName("nodes")
    private final Map<Integer, ConanGraphInfoGraphNode> nodeMap;

    public ConanGraphInfoGraph(Map<Integer, ConanGraphInfoGraphNode> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public Map<Integer, ConanGraphInfoGraphNode> getNodeMap() {
        return nodeMap;
    }
}
