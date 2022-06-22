package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.util.NameVersion;

public class GitConfigResult {
    private final NameVersion nameVersion;
    private final String remoteUrl;
    @Nullable
    private final String branch; // Nullable in case of detached head

    public GitConfigResult(NameVersion nameVersion, String remoteUrl, @Nullable String branch) {
        this.nameVersion = nameVersion;
        this.remoteUrl = remoteUrl;
        this.branch = branch;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public Optional<String> getBranch() {
        return Optional.ofNullable(branch);
    }
}
