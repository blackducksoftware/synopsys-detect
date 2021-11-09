/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.project.options;

public class ProjectGroupOptions {
    private final String projectGroup;

    public ProjectGroupOptions(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectGroup() {
        return projectGroup;
    }
}
