package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GitConfigTest {

    @Test
    void fromGitConfigNodes() {
        //#region Create GitConfigNodes

        // The core node should be ignored by the transformation.
        final Map<String, String> coreProperties = new HashMap<>();
        coreProperties.put("repositoryformatversion", "0");
        coreProperties.put("filemode", "true");
        final GitConfigNode coreNode = new GitConfigNode("core", coreProperties);

        final Map<String, String> remoteProperties = new HashMap<>();
        remoteProperties.put("url", "https://github.com/blackducksoftware/synopsys-detect.git");
        remoteProperties.put("fetch", "+refs/heads/*:refs/remotes/origin/");
        final GitConfigNode remoteNode = new GitConfigNode("remote", "origin", remoteProperties);

        final Map<String, String> branchProperties = new HashMap<>();
        branchProperties.put("remote", "origin");
        branchProperties.put("merge", "refs/heads/master");
        final GitConfigNode branchNode = new GitConfigNode("branch", "master", branchProperties);

        final Map<String, String> anotherBranchProperties = new HashMap<>();
        anotherBranchProperties.put("remote", "origin");
        anotherBranchProperties.put("merge", "refs/heads/another-branch");
        final GitConfigNode anotherBranch = new GitConfigNode("branch", "another-branch", anotherBranchProperties);

        final List<GitConfigNode> gitConfigNodes = new ArrayList<>();
        gitConfigNodes.add(coreNode);
        gitConfigNodes.add(remoteNode);
        gitConfigNodes.add(branchNode);
        gitConfigNodes.add(anotherBranch);

        //#endregion Create GitConfigNodes

        final GitConfig gitConfig = GitConfig.fromGitConfigNodes(gitConfigNodes);

        Assertions.assertEquals(1, gitConfig.getGitConfigRemotes().size());

        final GitConfigRemote gitConfigRemote = gitConfig.getGitConfigRemotes().get(0);
        Assertions.assertEquals("origin", gitConfigRemote.getName());
        Assertions.assertEquals("https://github.com/blackducksoftware/synopsys-detect.git", gitConfigRemote.getUrl());
        Assertions.assertEquals("+refs/heads/*:refs/remotes/origin/", gitConfigRemote.getFetch());

        Assertions.assertEquals(2, gitConfig.getGitConfigBranches().size());

        final GitConfigBranch gitConfigBranch1 = gitConfig.getGitConfigBranches().get(0);
        Assertions.assertEquals("master", gitConfigBranch1.getName());
        Assertions.assertEquals("origin", gitConfigBranch1.getRemoteName());
        Assertions.assertEquals("refs/heads/master", gitConfigBranch1.getMerge());

        final GitConfigBranch gitConfigAnotherBranch = gitConfig.getGitConfigBranches().get(1);
        Assertions.assertEquals("another-branch", gitConfigAnotherBranch.getName());
        Assertions.assertEquals("origin", gitConfigAnotherBranch.getRemoteName());
        Assertions.assertEquals("refs/heads/another-branch", gitConfigAnotherBranch.getMerge());
    }
}