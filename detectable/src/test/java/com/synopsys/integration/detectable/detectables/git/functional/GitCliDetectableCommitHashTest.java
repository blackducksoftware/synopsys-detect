package com.synopsys.integration.detectable.detectables.git.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.ExecutableOutputUtil;

public class GitCliDetectableCommitHashTest extends DetectableFunctionalTest {
    public GitCliDetectableCommitHashTest() throws IOException {
        super("git-cli");
    }

    @Override
    public void setup() throws IOException {
        addDirectory(Paths.get(".git"));

        addExecutableOutput(ExecutableOutputUtil.success("https://github.com/blackducksoftware/synopsys-detect"), "git", "config", "--get", "remote.origin.url");

        addExecutableOutput(ExecutableOutputUtil.success("HEAD"), "git", "rev-parse", "--abbrev-ref", "HEAD");

        addExecutableOutput(ExecutableOutputUtil.success("(HEAD -> develop, origin/develop, origin/HEAD)"), "git", "log", "-n", "1", "--pretty=%d", "HEAD");

        addExecutableOutput(ExecutableOutputUtil.success("9ec2a2bcfa8651b6e096b06d72b1b9290b429e3c"), "git", "rev-parse", "HEAD");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createGitDetectable(environment, () -> ExecutableTarget.forCommand("git"));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.getProjectName());
        Assertions.assertEquals("9ec2a2bcfa8651b6e096b06d72b1b9290b429e3c", extraction.getProjectVersion());
    }
}