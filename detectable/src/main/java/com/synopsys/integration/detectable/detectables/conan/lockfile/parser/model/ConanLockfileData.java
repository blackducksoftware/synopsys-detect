package com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ConanLockfileData extends Stringable {
    @SerializedName("graph_lock")
    private final ConanLockfileGraph conanLockfileGraph;
    private final String version;

    public ConanLockfileData(ConanLockfileGraph conanLockfileGraph, String version) {
        this.conanLockfileGraph = conanLockfileGraph;
        this.version = version;
    }

    public ConanLockfileGraph getConanLockfileGraph() {
        return conanLockfileGraph;
    }

    public String getVersion() {
        return version;
    }
}
