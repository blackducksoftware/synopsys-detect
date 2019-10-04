package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.BazelWorkspace;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;

public class BazelWorkspaceTest {

    @Test
    public void test() {
        final File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE");
        final BazelWorkspace bazelWorkspace = new BazelWorkspace(workspaceFile);

        assertEquals(WorkspaceRule.MAVEN_INSTALL, bazelWorkspace.getDependencyRule());
    }
}
