package com.blackducksoftware.integration.hub.detect.workflow.project;

public class ProjectNameVersionOptions {
    public final String sourcePathName;
    public final String projectBomTool;
    public final String overrideProjectName;
    public final String overrideProjectVersionName;
    public final String defaultProjectVersionText;
    public final String defaultProjectVersionScheme;
    public final String defaultProjectVersionFormat;

    public ProjectNameVersionOptions(final String sourcePathName, final String projectBomTool, final String overrideProjectName, final String overrideProjectVersionName, final String defaultProjectVersionText,
        final String defaultProjectVersionScheme, final String defaultProjectVersionFormat) {

        this.sourcePathName = sourcePathName;
        this.projectBomTool = projectBomTool;
        this.overrideProjectName = overrideProjectName;
        this.overrideProjectVersionName = overrideProjectVersionName;
        this.defaultProjectVersionText = defaultProjectVersionText;
        this.defaultProjectVersionScheme = defaultProjectVersionScheme;
        this.defaultProjectVersionFormat = defaultProjectVersionFormat;
    }
}
