package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.exception.IntegrationException;

public class BazelDetectableOptionsTest {

    @Test
    public void testOneProvided() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_INSTALL)));
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testThreeProvided() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(
            Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_JAR), FilterableEnumValue.value(WorkspaceRule.MAVEN_INSTALL), FilterableEnumValue.value(WorkspaceRule.HASKELL_CABAL_LIBRARY)));
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testAllValue() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_JAR), FilterableEnumValue.allValue(), FilterableEnumValue.value(WorkspaceRule.HASKELL_CABAL_LIBRARY)));
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testNoneValue() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.noneValue()));
        assertEquals(0, chosenWorkspaceRules.size());
    }

    @Test
    public void testNoneValueWithARealValue() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_JAR), FilterableEnumValue.noneValue()));
        assertEquals(0, chosenWorkspaceRules.size());
    }

    private Set<WorkspaceRule> run(List<FilterableEnumValue<WorkspaceRule>> providedBazelDependencyRule) throws IntegrationException {
        BazelDetectableOptions options = new BazelDetectableOptions("//:testTarget", providedBazelDependencyRule, null);
        return options.getBazelDependencyRules();
    }
}
