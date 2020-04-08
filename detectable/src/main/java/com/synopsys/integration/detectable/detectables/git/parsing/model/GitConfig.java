package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.List;
import java.util.stream.Collectors;

// A List<GitConfigNode> is a rough parsing result. This class provides a better API.
public class GitConfig {
    // Only including remote and branch nodes since the core node is not being used.
    private final List<GitConfigRemote> gitConfigRemotes;
    private final List<GitConfigBranch> gitConfigBranches;

    public static GitConfig fromGitConfigNodes(final List<GitConfigNode> gitConfigNodes) {
        final List<GitConfigRemote> gitConfigRemotes = gitConfigNodes.stream()
                                                           .filter(node -> node.getType().equals("remote"))
                                                           .map(node -> {
                                                               String remoteNodeName = node.getName().orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a name."));
                                                               String remoteNodeUrl = node.getProperty("url").orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a url field."));
                                                               String remoteNodeFetch = node.getProperty("fetch").orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a fetch field."));
                                                               return new GitConfigRemote(remoteNodeName, remoteNodeUrl, remoteNodeFetch);
                                                           })
                                                           .collect(Collectors.toList());

        final List<GitConfigBranch> gitConfigBranches = gitConfigNodes.stream()
                                                            .filter(node -> node.getType().equals("branch"))
                                                            .map(node -> {
                                                                String remoteNodeName = node.getName().orElseThrow(() -> new IllegalArgumentException("Expected branch node to have a name."));
                                                                String remoteNodeRemote = node.getProperty("remote").orElseThrow(() -> new IllegalArgumentException("Expected branch node to have a remote field."));
                                                                String remoteNodeMerge = node.getProperty("merge").orElseThrow(() -> new IllegalArgumentException("Expected branch node to have a fetch field."));
                                                                return new GitConfigBranch(remoteNodeName, remoteNodeRemote, remoteNodeMerge);
                                                            })
                                                            .collect(Collectors.toList());

        return new GitConfig(gitConfigRemotes, gitConfigBranches);
    }

    public GitConfig(final List<GitConfigRemote> gitConfigRemotes, final List<GitConfigBranch> gitConfigBranches) {
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
