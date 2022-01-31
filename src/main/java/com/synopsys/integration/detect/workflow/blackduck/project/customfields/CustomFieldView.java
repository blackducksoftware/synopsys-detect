package com.synopsys.integration.detect.workflow.blackduck.project.customfields;

import java.util.List;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;

public class CustomFieldView extends BlackDuckView {
    private int position;
    private String label;
    private List<String> values;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
