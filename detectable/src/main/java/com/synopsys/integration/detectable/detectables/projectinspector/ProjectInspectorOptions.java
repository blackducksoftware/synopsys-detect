package com.synopsys.integration.detectable.detectables.projectinspector;

import org.jetbrains.annotations.Nullable;

public class ProjectInspectorOptions {
    private final String additionalArguments;

    public ProjectInspectorOptions(@Nullable String additionalArguments) {
        this.additionalArguments = additionalArguments;
    }

    @Nullable
    public String getAdditionalArguments() {
        return additionalArguments;
    }
}
