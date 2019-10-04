package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.Workspace;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;

public class WorkspaceTest {

    @Test
    public void test() {
        final File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE");
        final Workspace workspace = new Workspace(workspaceFile);

        assertEquals(WorkspaceRule.MAVEN_INSTALL, workspace.getDependencyRule());
        assertEquals("maven_install", workspace.getDependencyRule().getName());

    }
}
