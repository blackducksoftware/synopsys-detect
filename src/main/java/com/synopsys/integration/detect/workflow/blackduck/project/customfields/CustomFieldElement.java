package com.synopsys.integration.detect.workflow.blackduck.project.customfields;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldElement {
    private String label = "";
    private List<String> value = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
