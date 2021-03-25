package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.io.File;

public class ToolDecisionBuilder {
    private BazelData bazelData;

    public ToolDecisionBuilder runBazel(File sourceDirectory) {
        this.bazelData = new BazelData(sourceDirectory);
        return this;
    }

    public ToolDecision build() {
        return new ToolDecision(bazelData);
    }
}