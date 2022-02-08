package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.ArrayList;
import java.util.List;

public class GradleReport {
    private String projectSourcePath = "";
    private String projectGroup = "";
    private String projectName = "";
    private String projectVersionName = "";
    private List<GradleConfiguration> configurations = new ArrayList<>();

    public String getProjectSourcePath() {
        return projectSourcePath;
    }

    public void setProjectSourcePath(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public void setProjectVersionName(String projectVersionName) {
        this.projectVersionName = projectVersionName;
    }

    public List<GradleConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<GradleConfiguration> configurations) {
        this.configurations = configurations;
    }
}
