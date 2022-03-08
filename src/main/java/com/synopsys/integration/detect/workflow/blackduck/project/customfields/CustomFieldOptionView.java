package com.synopsys.integration.detect.workflow.blackduck.project.customfields;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;

public class CustomFieldOptionView extends BlackDuckView {
    private int position;
    private String label;

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
}
