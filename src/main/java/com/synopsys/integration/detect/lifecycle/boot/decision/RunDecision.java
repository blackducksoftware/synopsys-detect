package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.detect.configuration.enumeration.DetectTargetType;

//Basically this is the placeholder for these decisions. Eventually a more fully formed decision will be made. -jp 3/29/20
public class RunDecision {
    private final boolean isDockerMode;
    private final DetectTargetType dockerMode;

    public RunDecision(boolean isDockerMode, DetectTargetType detectTargetType) {
        this.isDockerMode = isDockerMode;
        this.dockerMode = detectTargetType;
    }

    public boolean isDockerMode() {
        return isDockerMode;
    }

    public DetectTargetType getDockerMode() {
        return dockerMode;
    }
}
