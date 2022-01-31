package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNodeTransformer;

class GitConfigNodeTransformerTest {

    @Test
    void createGitConfig() {
        //#region Create GitConfigNodes
        // The core node should be ignored by the transformation.
        Map<String, String> coreProperties = new HashMap<>();
        coreProperties.put("repositoryformatversion", "0");
        coreProperties.put("filemode", "true");
        GitConfigNode coreNode = new GitConfigNode("core", coreProperties);

        Map<String, String> remoteProperties = new HashMap<>();
        remoteProperties.put("url", "https://github.com/blackducksoftware/synopsys-detect.git");
        remoteProperties.put("fetch", "+refs/heads/*:refs/remotes/origin/");
        GitConfigNode remoteNode = new GitConfigNode("remote", "origin", remoteProperties);

        Map<String, String> branchProperties = new HashMap<>();
        branchProperties.put("remote", "origin");
        branchProperties.put("merge", "refs/heads/master");
        GitConfigNode branchNode = new GitConfigNode("branch", "master", branchProperties);

        Map<String, String> anotherBranchProperties = new HashMap<>();
        anotherBranchProperties.put("remote", "origin");
        anotherBranchProperties.put("merge", "refs/heads/another-branch");
        GitConfigNode anotherBranch = new GitConfigNode("branch", "another-branch", anotherBranchProperties);

        List<GitConfigNode> gitConfigNodes = new ArrayList<>();
        gitConfigNodes.add(coreNode);
        gitConfigNodes.add(remoteNode);
        gitConfigNodes.add(branchNode);
        gitConfigNodes.add(anotherBranch);
        //#endregion Create GitConfigNodes

        GitConfigNodeTransformer gitConfigNodeTransformer = new GitConfigNodeTransformer();
        GitConfig gitConfig = gitConfigNodeTransformer.createGitConfig(gitConfigNodes);

        Assertions.assertEquals(1, gitConfig.getGitConfigRemotes().size());

        GitConfigRemote gitConfigRemote = gitConfig.getGitConfigRemotes().get(0);
        Assertions.assertEquals("origin", gitConfigRemote.getName());
        Assertions.assertEquals("https://github.com/blackducksoftware/synopsys-detect.git", gitConfigRemote.getUrl());
        Assertions.assertEquals("+refs/heads/*:refs/remotes/origin/", gitConfigRemote.getFetch());

        Assertions.assertEquals(2, gitConfig.getGitConfigBranches().size());

        GitConfigBranch gitConfigBranch1 = gitConfig.getGitConfigBranches().get(0);
        Assertions.assertEquals("master", gitConfigBranch1.getName());
        Assertions.assertEquals("origin", gitConfigBranch1.getRemoteName());
        Assertions.assertEquals("refs/heads/master", gitConfigBranch1.getMerge());

        GitConfigBranch gitConfigAnotherBranch = gitConfig.getGitConfigBranches().get(1);
        Assertions.assertEquals("another-branch", gitConfigAnotherBranch.getName());
        Assertions.assertEquals("origin", gitConfigAnotherBranch.getRemoteName());
        Assertions.assertEquals("refs/heads/another-branch", gitConfigAnotherBranch.getMerge());
    }
}