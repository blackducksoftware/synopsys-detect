package com.synopsys.integration.detectable.detectables.sbt.parse.model;

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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public ExternalId getProjectExternalId() {
        return projectExternalId;
    }

    public void setProjectExternalId(ExternalId projectExternalId) {
        this.projectExternalId = projectExternalId;
    }

    public List<SbtDependencyModule> getModules() {
        return modules;
    }

    public void setModules(List<SbtDependencyModule> modules) {
        this.modules = modules;
    }
}
