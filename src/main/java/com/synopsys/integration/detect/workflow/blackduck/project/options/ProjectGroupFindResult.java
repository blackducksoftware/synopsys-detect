package com.synopsys.integration.detect.workflow.blackduck.project.options;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.rest.HttpUrl;

public class ProjectGroupFindResult {
    @Nullable
    private final HttpUrl projectGroup;

    public ProjectGroupFindResult(@Nullable HttpUrl projectGroup) {
        this.projectGroup = projectGroup;
    }

    public static ProjectGroupFindResult skip() {
        return new ProjectGroupFindResult(null);
    }

    public static ProjectGroupFindResult of(@NotNull HttpUrl cloneUrl) {
        return new ProjectGroupFindResult(cloneUrl);
    }

    public Optional<HttpUrl> getProjectGroup() {
        return Optional.ofNullable(projectGroup);
    }
}
