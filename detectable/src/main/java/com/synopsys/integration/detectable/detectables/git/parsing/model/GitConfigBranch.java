/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
