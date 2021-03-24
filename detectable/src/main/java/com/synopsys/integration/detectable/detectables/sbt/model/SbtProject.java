/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.model;

import java.util.List;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class SbtProject {
    private String projectName;
    private String projectVersion;
    private ExternalId projectExternalId;
    private List<SbtDependencyModule> modules;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(final String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public ExternalId getProjectExternalId() {
        return projectExternalId;
    }

    public void setProjectExternalId(final ExternalId projectExternalId) {
        this.projectExternalId = projectExternalId;
    }

    public List<SbtDependencyModule> getModules() {
        return modules;
    }

    public void setModules(final List<SbtDependencyModule> modules) {
        this.modules = modules;
    }
}
