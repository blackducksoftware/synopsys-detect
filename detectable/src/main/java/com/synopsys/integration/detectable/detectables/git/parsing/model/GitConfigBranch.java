package com.synopsys.integration.detectable.detectables.git.parsing.model;

import org.jetbrains.annotations.NotNull;

public class GitConfigBranch {
    @NotNull
    private final String name;
    @NotNull
    private final String remoteName;
    @NotNull
    private final String merge;

    public GitConfigBranch(@NotNull final String name, @NotNull final String remoteName, @NotNull final String merge) {
        this.name = name;
        this.remoteName = remoteName;
        this.merge = merge;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getRemoteName() {
        return remoteName;
    }

    @NotNull
    public String getMerge() {
        return merge;
    }
}
