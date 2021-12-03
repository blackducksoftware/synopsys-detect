package com.synopsys.integration.detectable.detectables.git.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigNode;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;

class GitFileParserTest {
    @Test
    public void parseHeadFile() {
        GitFileParser gitFileParser = new GitFileParser();
        final String gitHeadContent = "ref: refs/heads/master\n";
        String head = gitFileParser.parseGitHead(gitHeadContent);
        Assertions.assertEquals("refs/heads/master", head);
    }

    @Test
    public void parseGitConfig() {
        GitFileParser gitFileParser = new GitFileParser();
        List<String> output = Arrays.asList(
            "[core]",
            "	repositoryformatversion = 0",
            "	filemode = true",
            "	bare = false",
            "	logallrefupdates = true",
            "	ignorecase = true",
            "	precomposeunicode = true",
            "[remote \"origin\"]",
            "	url = https://github.com/blackducksoftware/synopsys-detect.git",
            "	fetch = +refs/heads/*:refs/remotes/origin/*",
            "[branch \"master\"]",
            "	remote = origin",
            "	merge = refs/heads/master",
            "[branch \"test\"]",
            "	remote = origin",
            "	merge = refs/heads/test"
        );

        List<GitConfigNode> gitConfigNodes = gitFileParser.parseGitConfig(output);
        Assertions.assertEquals(4, gitConfigNodes.size());

        List<GitConfigNode> gitConfigCores = getNodes(gitConfigNodes, "core");
        Assertions.assertEquals(1, gitConfigCores.size());

        List<GitConfigNode> gitConfigRemotes = getNodes(gitConfigNodes, "remote");
        Assertions.assertEquals(1, gitConfigRemotes.size());

        List<GitConfigNode> gitConfigBranches = getNodes(gitConfigNodes, "branch");
        Assertions.assertEquals(2, gitConfigBranches.size());

        Optional<GitConfigNode> remoteOrigin = gitConfigRemotes.stream()
            .filter(it -> it.getName().isPresent())
            .filter(it -> it.getName().get().equals("origin"))
            .findAny();

        Assertions.assertTrue(remoteOrigin.isPresent());

        Optional<String> fetch = remoteOrigin.get().getProperty("fetch");
        Assertions.assertEquals(Optional.of("+refs/heads/*:refs/remotes/origin/*"), fetch);
    }

    @NotNull
    private List<GitConfigNode> getNodes(@NotNull List<GitConfigNode> gitConfigElements, @NotNull String tag) {
        return gitConfigElements.stream()
            .filter(gitConfigElement -> tag.equals(gitConfigElement.getType()))
            .collect(Collectors.toList());
    }
}