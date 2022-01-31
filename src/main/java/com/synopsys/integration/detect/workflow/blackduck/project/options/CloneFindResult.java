package com.synopsys.integration.detect.workflow.blackduck.project.options;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.rest.HttpUrl;

public class CloneFindResult {
    @Nullable
    private final HttpUrl cloneUrl;

    public CloneFindResult(@Nullable HttpUrl cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public static CloneFindResult empty() {
        return new CloneFindResult(null);
    }

    public static CloneFindResult of(@NotNull HttpUrl cloneUrl) {
        return new CloneFindResult(cloneUrl);
    }

    public Optional<HttpUrl> getCloneUrl() {
        return Optional.ofNullable(cloneUrl);
    }
}
