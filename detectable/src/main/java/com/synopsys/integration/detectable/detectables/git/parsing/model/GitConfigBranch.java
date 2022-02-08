package com.synopsys.integration.detectable.detectables.git.parsing.model;

import org.jetbrains.annotations.NotNull;

public class GitConfigBranch {
    @NotNull
    private final String name;
    @NotNull
    private final String remoteName;
    @NotNull
    private final String merge;

    public GitConfigBranch(@NotNull String name, @NotNull String remoteName, @NotNull String merge) {
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
