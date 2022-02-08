package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.List;

// A List<GitConfigNode> is a rough parsing result. This class provides a better API.
public class GitConfig {
    // Only including remote and branch nodes since the core node is not being used.
    private final List<GitConfigRemote> gitConfigRemotes;
    private final List<GitConfigBranch> gitConfigBranches;

    public GitConfig(List<GitConfigRemote> gitConfigRemotes, List<GitConfigBranch> gitConfigBranches) {
        this.gitConfigRemotes = gitConfigRemotes;
        this.gitConfigBranches = gitConfigBranches;
    }

    public List<GitConfigRemote> getGitConfigRemotes() {
        return gitConfigRemotes;
    }

    public List<GitConfigBranch> getGitConfigBranches() {
        return gitConfigBranches;
    }
}
