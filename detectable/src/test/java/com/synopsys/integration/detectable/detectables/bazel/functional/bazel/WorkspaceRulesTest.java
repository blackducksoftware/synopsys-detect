package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRules;
import com.synopsys.integration.exception.IntegrationException;

public class WorkspaceRulesTest {

    @Test
    public void test() throws IntegrationException {
        final File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE");
        final WorkspaceRules workspaceRules = new WorkspaceRules(workspaceFile);

        assertEquals("maven_install", workspaceRules.getDependencyRule());

    }
}
