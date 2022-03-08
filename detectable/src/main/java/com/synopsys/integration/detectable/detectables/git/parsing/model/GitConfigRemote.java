package com.synopsys.integration.detectable.detectables.git.parsing.model;

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
