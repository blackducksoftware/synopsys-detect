package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.BazelWorkspace;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;

class BazelWorkspaceTest {

    @Test
    void testSingleRule() {
        File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE");
        BazelWorkspace bazelWorkspace = new BazelWorkspace(workspaceFile);

        assertEquals(Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL), bazelWorkspace.getDependencyRuleTypes());
    }

    @Test
    void testMultipleRules() {
        File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE_multipleRules");
        BazelWorkspace bazelWorkspace = new BazelWorkspace(workspaceFile);

        Set<WorkspaceRule> rulesFound = bazelWorkspace.getDependencyRuleTypes();
        assertEquals(Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL, WorkspaceRule.HASKELL_CABAL_LIBRARY), rulesFound);
    }
}
