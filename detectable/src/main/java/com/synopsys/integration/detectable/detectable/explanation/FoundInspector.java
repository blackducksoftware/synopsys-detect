package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

public class FoundInspector extends Explanation {
    private final String inspectorDescription;

    public FoundInspector(File file) {
        this.inspectorDescription = file.toString();
    }

    public FoundInspector(String description) {
        this.inspectorDescription = description;
    }

    @Override
    public String describeSelf() {
        return "Found inspector: " + inspectorDescription;
    }
}
