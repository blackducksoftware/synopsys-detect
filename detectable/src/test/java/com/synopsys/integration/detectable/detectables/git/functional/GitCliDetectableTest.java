package com.synopsys.integration.detectable.detectables.git.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.executable.ExecutableOutput;

public class GitCliDetectableTest extends DetectableFunctionalTest {
    public GitCliDetectableTest() throws IOException {
        super("git-cli");
    }

    @Override
    public void setup() throws IOException {
        addDirectory(Paths.get(".git"));

        String gitRemoteUrlOutput = "https://github.com/blackducksoftware/synopsys-detect";
        ExecutableOutput gitConfigExecutableOutput = new ExecutableOutput(0, gitRemoteUrlOutput, "");
        addExecutableOutput(gitConfigExecutableOutput, "git", "config", "--get", "remote.origin.url");

        String gitBranchOutput = "branch-version";
        ExecutableOutput gitBranchExecutableOutput = new ExecutableOutput(0, gitBranchOutput, "");
        addExecutableOutput(gitBranchExecutableOutput, "git", "rev-parse", "--abbrev-ref", "HEAD");

        String gitCommitOutput = "c173aa3ea902e8ccef6d78137f3a48e275a1a820";
        ExecutableOutput gitCommitExecutableOutput = new ExecutableOutput(0, gitCommitOutput, "");
        addExecutableOutput(gitCommitExecutableOutput, "git", "rev-parse", "HEAD");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        class GitExeResolver implements GitResolver {
            @Override
            public ExecutableTarget resolveGit() {
                return ExecutableTarget.forCommand("git");
            }
        }

        return detectableFactory.createGitDetectable(environment, new GitExeResolver());
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.getProjectName());
        Assertions.assertEquals("branch-version", extraction.getProjectVersion());
    }
}