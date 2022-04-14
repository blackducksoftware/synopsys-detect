package com.synopsys.integration.detectable.detectable.explanation;

import java.io.File;

import com.synopsys.integration.detectable.ExecutableTarget;

public class FoundInspector extends Explanation {
    private final String inspectorDescription;

    public FoundInspector(File file) {
        this.inspectorDescription = file.toString();
    }

    public FoundInspector(String description) {
        this.inspectorDescription = description;
    }

    public FoundInspector(ExecutableTarget executableTarget) {
        this.inspectorDescription = executableTarget.toCommand();
    }

    @Override
    public String describeSelf() {
        return "Found inspector: " + inspectorDescription;
    }
}
