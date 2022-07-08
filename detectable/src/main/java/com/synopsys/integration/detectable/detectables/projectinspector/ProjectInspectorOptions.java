package com.synopsys.integration.detectable.detectables.projectinspector;

import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class ProjectInspectorOptions {
    @Nullable
    private final Path projectInspectorZipPath;

    @Nullable
    private final String additionalArguments;

    public ProjectInspectorOptions(@Nullable Path projectInspectorZipPath, @Nullable String additionalArguments) {
        this.projectInspectorZipPath = projectInspectorZipPath;
        this.additionalArguments = additionalArguments;
    }

    @Nullable
    public String getAdditionalArguments() {
        return additionalArguments;
    }

    public Optional<Path> getProjectInspectorZipPath() {
        return Optional.ofNullable(projectInspectorZipPath);
    }
}
