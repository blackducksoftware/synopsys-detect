package com.synopsys.integration.detectable.detectables.ivy.task;

public class IvyTaskDetectableOptions {
    private final String ivyDependencytreeTarget;

    public IvyTaskDetectableOptions(String ivyDependencytreeTarget) {
        this.ivyDependencytreeTarget = ivyDependencytreeTarget;
    }

    public String getIvyDependencytreeTarget() {
        return ivyDependencytreeTarget;
    }
}
