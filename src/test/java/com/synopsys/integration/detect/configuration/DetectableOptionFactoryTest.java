package com.synopsys.integration.detect.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;

class DetectableOptionFactoryTest {

    @Test
    void testBazelDependencyRulesNone() {
        List<FilterableEnumValue<WorkspaceRule>> userProvidedRulesValue =
            Arrays.asList(FilterableEnumValue.noneValue());
        Set<WorkspaceRule> derivedRules = doBazelDependencyRulesTest(userProvidedRulesValue);
        assertEquals(0, derivedRules.size());
    }

    @Test
    void testBazelDependencyRulesAll() {
        List<FilterableEnumValue<WorkspaceRule>> userProvidedRulesValue =
            Arrays.asList(FilterableEnumValue.allValue());
        Set<WorkspaceRule> derivedRules = doBazelDependencyRulesTest(userProvidedRulesValue);
        derivedRules.contains(WorkspaceRule.MAVEN_INSTALL);
        derivedRules.contains(WorkspaceRule.MAVEN_JAR);
        derivedRules.contains(WorkspaceRule.HASKELL_CABAL_LIBRARY);
    }

    @Test
    void testBazelDependencyRulesOne() {
        List<FilterableEnumValue<WorkspaceRule>> userProvidedRulesValue =
            Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_INSTALL));
        Set<WorkspaceRule> derivedRules = doBazelDependencyRulesTest(userProvidedRulesValue);
        assertEquals(1, derivedRules.size());
        derivedRules.contains(WorkspaceRule.MAVEN_INSTALL);
    }

    @Test
    void testBazelDependencyRulesTwo() {
        List<FilterableEnumValue<WorkspaceRule>> userProvidedRulesValue =
            Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_INSTALL),
                FilterableEnumValue.value(WorkspaceRule.HASKELL_CABAL_LIBRARY));
        Set<WorkspaceRule> derivedRules = doBazelDependencyRulesTest(userProvidedRulesValue);
        assertEquals(2, derivedRules.size());
        derivedRules.contains(WorkspaceRule.MAVEN_INSTALL);
        derivedRules.contains(WorkspaceRule.HASKELL_CABAL_LIBRARY);
    }

    private Set<WorkspaceRule> doBazelDependencyRulesTest(List<FilterableEnumValue<WorkspaceRule>> userProvidedRulesValue) {
        PropertyConfiguration detectConfiguration = Mockito.mock(PropertyConfiguration.class);
        DetectableOptionFactory factory = new DetectableOptionFactory(detectConfiguration,
            null, null, null, null);
        Mockito.when(detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_DEPENDENCY_RULE.getProperty()))
            .thenReturn(userProvidedRulesValue);
        BazelDetectableOptions options = factory.createBazelDetectableOptions();
        return options.getBazelDependencyRules();
    }
}
