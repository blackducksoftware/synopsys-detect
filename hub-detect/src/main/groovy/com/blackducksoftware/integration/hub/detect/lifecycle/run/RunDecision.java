package com.blackducksoftware.integration.hub.detect.lifecycle.run;

public class RunDecision {
    private boolean runBlackduck;
    private boolean runPolaris;

    public RunDecision(final boolean runBlackduck, final boolean runPolaris) {
        this.runBlackduck = runBlackduck;
        this.runPolaris = runPolaris;
    }

    public boolean willRunBlackduck(){
        return runBlackduck;
    }

    public boolean willRunPolaris(){
        return runPolaris;
    }
}
