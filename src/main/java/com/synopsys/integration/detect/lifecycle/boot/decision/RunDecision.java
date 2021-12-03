package com.synopsys.integration.detect.lifecycle.boot.decision;

//Basically this is the placeholder for these decisions. Eventually a more fully formed decision will be made. -jp 3/29/20
public class RunDecision {
    private final boolean isDockerMode;

    public RunDecision(boolean isDockerMode) {
        this.isDockerMode = isDockerMode;
    }

    public boolean isDockerMode() {
        return isDockerMode;
    }
}
