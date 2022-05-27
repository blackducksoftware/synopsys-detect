package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.exception.IntegrationException;

class WorkspaceRuleChooserTest {

    private static final Set<WorkspaceRule> WORKSPACE_RULES_JUST_MAVEN_INSTALL = Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL);
    private static final Set<WorkspaceRule> WORKSPACE_RULES_JUST_MAVEN_JAR = Sets.newHashSet(WorkspaceRule.MAVEN_JAR);
    private static final Set<WorkspaceRule> WORKSPACE_RULES_THREE = Sets.newHashSet(
        WorkspaceRule.MAVEN_INSTALL,
        WorkspaceRule.HASKELL_CABAL_LIBRARY,
        WorkspaceRule.MAVEN_JAR
    );
    private static final Set<WorkspaceRule> WORKSPACE_RULES_HASKELL = Sets.newHashSet(WorkspaceRule.HASKELL_CABAL_LIBRARY);

    @Test
    void testOneRuleParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(null, WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    void testThreeRulesParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(null, WORKSPACE_RULES_THREE);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    void testOneProvidedSameOneParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(WORKSPACE_RULES_JUST_MAVEN_INSTALL, WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    void testOneRuleProvidedDifferentOneParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(WORKSPACE_RULES_JUST_MAVEN_JAR, WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_jar", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    void testThreeProvidedOneParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(WORKSPACE_RULES_THREE, WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    private Set<WorkspaceRule> run(Set<WorkspaceRule> workspaceRulesFromProperty, Set<WorkspaceRule> parsedWorkspaceRules)
        throws IntegrationException {
        WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        Set<WorkspaceRule> chosenWorkspaceRules = workspaceRuleChooser.choose(parsedWorkspaceRules, workspaceRulesFromProperty);
        return chosenWorkspaceRules;
    }
}
