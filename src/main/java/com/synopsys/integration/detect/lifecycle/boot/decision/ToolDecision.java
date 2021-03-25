package com.synopsys.integration.detect.lifecycle.boot.decision;

public class ToolDecision {
    private final BazelData bazelData;

    public ToolDecision(final BazelData bazelData) {
        this.bazelData = bazelData;
    }

    public boolean shouldRunBazel() {
        return bazelData != null;
    }

    public BazelData getBazelData() {
        return bazelData;
    }
}
