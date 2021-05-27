/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git.model;

import org.jetbrains.annotations.NotNull;

public class GitConfigRemote {
    @NotNull
    private final String name;
    @NotNull
    private final String url;
    @NotNull
    private final String fetch;

    public GitConfigRemote(@NotNull String name, @NotNull String url, @NotNull String fetch) {
        this.name = name;
        this.url = url;
        this.fetch = fetch;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getFetch() {
        return fetch;
    }
}
