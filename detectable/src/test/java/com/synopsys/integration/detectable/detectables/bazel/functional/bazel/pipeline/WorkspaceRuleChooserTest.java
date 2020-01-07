package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.exception.IntegrationException;

public class WorkspaceRuleChooserTest {

    @Test
    public void testDerivedBazelDependencyRule() throws IntegrationException {
        final WorkspaceRule chosenWorkspaceRule = run(null);
        assertEquals("maven_install", chosenWorkspaceRule.getName());
    }

    @Test
    public void testProvidedBazelDependencyRule() throws IOException, IntegrationException {
        final WorkspaceRule chosenWorkspaceRule = run(WorkspaceRule.MAVEN_INSTALL);
        assertEquals("maven_install", chosenWorkspaceRule.getName());
    }

    @Test
    public void testOverriddenBazelDependencyRule() throws IOException, IntegrationException {
        final WorkspaceRule chosenWorkspaceRule = run(WorkspaceRule.MAVEN_JAR);
        assertEquals("maven_jar", chosenWorkspaceRule.getName());
    }

    private WorkspaceRule run(final WorkspaceRule providedBazelDependencyRule) throws IntegrationException {
        final WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        final WorkspaceRule chosenWorkspaceRule = workspaceRuleChooser.choose(WorkspaceRule.MAVEN_INSTALL, providedBazelDependencyRule);
        return chosenWorkspaceRule;
    }
}
