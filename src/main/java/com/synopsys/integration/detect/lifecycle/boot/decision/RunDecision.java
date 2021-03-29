/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.decision;

//Basically this is the placeholder for these decisions. Eventually a more fully formed decision will be made. -jp 3/29/20
public class RunDecision {
    private final boolean isRapid;
    private final boolean isDockerMode;

    public RunDecision(final boolean isRapid, final boolean isDockerMode) {
        this.isRapid = isRapid;
        this.isDockerMode = isDockerMode;
    }

    public boolean isRapid() {
        return isRapid;
    }

    public boolean isDockerMode() {
        return isDockerMode;
    }
}
