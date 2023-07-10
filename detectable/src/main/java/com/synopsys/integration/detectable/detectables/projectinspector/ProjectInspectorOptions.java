package com.synopsys.integration.detectable.detectables.projectinspector;

import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class ProjectInspectorOptions {
    @Nullable
    private final Path projectInspectorZipPath;

    @Nullable
    private final String additionalArguments;

    @Nullable
    private final String globalArguments;

    public ProjectInspectorOptions(@Nullable Path projectInspectorZipPath,
                                   @Nullable String additionalArguments,
                                   @Nullable String globalArguments) {
        this.projectInspectorZipPath = projectInspectorZipPath;
        this.additionalArguments = additionalArguments;
        this.globalArguments = globalArguments;
    }

    @Nullable
    public String getAdditionalArguments() {
        return additionalArguments;
    }

    public Optional<Path> getProjectInspectorZipPath() {
        return Optional.ofNullable(projectInspectorZipPath);
    }

    @Nullable
    public String getGlobalArguments() {
        return globalArguments;
    }
}
