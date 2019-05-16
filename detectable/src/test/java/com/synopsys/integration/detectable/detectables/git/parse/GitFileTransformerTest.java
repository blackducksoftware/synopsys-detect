package com.synopsys.integration.detectable.detectables.git.parse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.model.GitConfigElement;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

class GitFileTransformerTest {

    @Test
    void transform() throws MalformedURLException, IntegrationException {
        final Map<String, String> remoteProperties = new HashMap<>();
        remoteProperties.put("url", "https://github.com/blackducksoftware/blackduck-artifactory.git");
        remoteProperties.put("fetch", "+refs/heads/*:refs/remotes/origin/");
        final GitConfigElement remote = new GitConfigElement("remote", "origin", remoteProperties);

        final Map<String, String> branchProperties = new HashMap<>();
        branchProperties.put("remote", "origin");
        branchProperties.put("merge", "refs/heads/master");
        final GitConfigElement branch = new GitConfigElement("branch", "master", branchProperties);

        final Map<String, String> badBranchProperties = new HashMap<>();
        badBranchProperties.put("remote", "origin");
        badBranchProperties.put("merge", "refs/heads/bad-branch");
        final GitConfigElement badBranch = new GitConfigElement("branch", "bad-branch", badBranchProperties);

        final String gitHead = "refs/heads/master";
        final List<GitConfigElement> gitConfigElements = new ArrayList<>();
        gitConfigElements.add(remote);
        gitConfigElements.add(branch);
        gitConfigElements.add(badBranch);

        final GitFileTransformer gitFileTransformer = new GitFileTransformer();
        final NameVersion nameVersion = gitFileTransformer.transform(gitConfigElements, gitHead);

        Assertions.assertEquals("blackducksoftware/blackduck-artifactory", nameVersion.getName());
        Assertions.assertEquals("master", nameVersion.getVersion());
    }
}