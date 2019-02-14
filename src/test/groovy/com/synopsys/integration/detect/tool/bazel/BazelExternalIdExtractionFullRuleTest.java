package com.synopsys.integration.detect.tool.bazel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BazelExternalIdExtractionFullRuleTest {

    @Test
    public void test() {
        BazelExternalIdExtractionSimpleRule simpleRule = new BazelExternalIdExtractionSimpleRule("@.*:jar", "maven_jar",
            "artifactory", ":");
        BazelExternalIdExtractionFullRule xPathRule = RuleConverter.simpleToFull(simpleRule);

        assertEquals(2, xPathRule.getTargetDependenciesQueryBazelCmdArguments().size());
        assertEquals("query", xPathRule.getTargetDependenciesQueryBazelCmdArguments().get(0));
        assertEquals("filter(\"@.*:jar\", deps(${detect.bazel.target}))", xPathRule.getTargetDependenciesQueryBazelCmdArguments().get(1));
        assertEquals(3, xPathRule.getDependencyToBazelExternalIdTransforms().size());
        assertEquals("^@", xPathRule.getDependencyToBazelExternalIdTransforms().get(0).getSearchRegex());
        assertEquals("", xPathRule.getDependencyToBazelExternalIdTransforms().get(0).getReplacementString());
        assertEquals("//.*", xPathRule.getDependencyToBazelExternalIdTransforms().get(1).getSearchRegex());
        assertEquals("", xPathRule.getDependencyToBazelExternalIdTransforms().get(1).getReplacementString());
        assertEquals("^", xPathRule.getDependencyToBazelExternalIdTransforms().get(2).getSearchRegex());
        assertEquals("//external:", xPathRule.getDependencyToBazelExternalIdTransforms().get(2).getReplacementString());
        assertEquals(4, xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().size());
        assertEquals("query", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().get(0));
        assertEquals("kind(maven_jar, ${detect.bazel.target.dependency})", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().get(1));
        assertEquals("--output", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().get(2));
        assertEquals("xml", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().get(3));
        assertEquals("/query/rule[@class='maven_jar']/string[@name='artifactory']", xPathRule.getXPathQuery());
        assertEquals(":", xPathRule.getArtifactStringSeparatorRegex());
        assertEquals("value", xPathRule.getRuleElementValueAttrName());
    }
}
