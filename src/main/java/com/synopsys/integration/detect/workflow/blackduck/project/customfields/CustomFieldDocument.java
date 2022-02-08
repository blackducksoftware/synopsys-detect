package com.synopsys.integration.detect.workflow.blackduck.project.customfields;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldDocument {
    private List<CustomFieldElement> project = new ArrayList<>();
    private List<CustomFieldElement> version = new ArrayList<>();

    public List<CustomFieldElement> getProject() {
        return project;
    }

    public void setProject(List<CustomFieldElement> project) {
        this.project = project;
    }

    public List<CustomFieldElement> getVersion() {
        return version;
    }

    public void setVersion(List<CustomFieldElement> version) {
        this.version = version;
    }
}
