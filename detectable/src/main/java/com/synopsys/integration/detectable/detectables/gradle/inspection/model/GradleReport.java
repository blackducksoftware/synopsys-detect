/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public void setProjectSourcePath(final String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(final String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public void setProjectVersionName(final String projectVersionName) {
        this.projectVersionName = projectVersionName;
    }

    public List<GradleConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(final List<GradleConfiguration> configurations) {
        this.configurations = configurations;
    }
}
