package com.synopsys.integration.detect.workflow.project;

public class ProjectNameVersionOptions {
    public final String sourcePathName;
    public final String overrideProjectName;
    public final String overrideProjectVersionName;

    public ProjectNameVersionOptions(String sourcePathName, String overrideProjectName, String overrideProjectVersionName) {
        this.sourcePathName = sourcePathName;
        this.overrideProjectName = overrideProjectName;
        this.overrideProjectVersionName = overrideProjectVersionName;
    }
}
