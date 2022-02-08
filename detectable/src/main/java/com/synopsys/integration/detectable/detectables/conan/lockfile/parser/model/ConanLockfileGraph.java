package com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ConanLockfileGraph extends Stringable {
    @SerializedName("nodes")
    private final Map<Integer, ConanLockfileNode> nodeMap;
    @SerializedName("revisions_enabled")
    private final boolean revisionsEnabled;

    public ConanLockfileGraph(Map<Integer, ConanLockfileNode> nodeMap, boolean revisionsEnabled) {
        this.nodeMap = nodeMap;
        this.revisionsEnabled = revisionsEnabled;
    }

    public Map<Integer, ConanLockfileNode> getNodeMap() {
        return nodeMap;
    }

    public boolean isRevisionsEnabled() {
        return revisionsEnabled;
    }
}
