package com.blackducksoftware.integration.hub.detect.detector.bazel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BazelExternalIdExtractionXPathRuleTest {

    @Test
    public void test() {
        BazelExternalIdExtractionSimpleRule simpleRule = new BazelExternalIdExtractionSimpleRule("@.*:jar", "maven_jar",
            "artifactory", ":", "//foo:foolib");
        BazelExternalIdExtractionXPathRule xPathRule = new BazelExternalIdExtractionXPathRule(simpleRule);

        assertEquals(2, xPathRule.getTargetDependenciesQueryBazelCmdArguments().size());
        assertEquals("query", xPathRule.getTargetDependenciesQueryBazelCmdArguments().get(0));
        assertEquals("filter(\"@.*:jar\", deps(//foo:foolib))", xPathRule.getTargetDependenciesQueryBazelCmdArguments().get(1));
        assertEquals(3, xPathRule.getDependencyToBazelExternalIdTransforms().size());
        assertEquals("^@", xPathRule.getDependencyToBazelExternalIdTransforms().get(0).getSearchRegex());
        assertEquals("", xPathRule.getDependencyToBazelExternalIdTransforms().get(0).getReplacementString());
        assertEquals("//.*", xPathRule.getDependencyToBazelExternalIdTransforms().get(1).getSearchRegex());
        assertEquals("", xPathRule.getDependencyToBazelExternalIdTransforms().get(1).getReplacementString());
        assertEquals("^", xPathRule.getDependencyToBazelExternalIdTransforms().get(2).getSearchRegex());
        assertEquals("//external:", xPathRule.getDependencyToBazelExternalIdTransforms().get(2).getReplacementString());
        assertEquals(2, xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().size());
        assertEquals("query", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().get(0));
        assertEquals("kind(maven_jar, ${detect.bazel.target.dependency})", xPathRule.getDependencyDetailsXmlQueryBazelCmdArguments().get(1));
        assertEquals("/query/rule[@class='maven_jar']/string[@name='artifactory']", xPathRule.getXPathQuery());
        assertEquals(":", xPathRule.getArtifactStringSeparatorRegex());
        assertEquals("value", xPathRule.getRuleElementValueAttrName());
    }
}
