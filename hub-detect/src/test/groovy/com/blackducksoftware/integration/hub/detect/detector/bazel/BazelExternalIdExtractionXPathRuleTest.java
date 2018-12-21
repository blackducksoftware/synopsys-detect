package com.blackducksoftware.integration.hub.detect.detector.bazel;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class BazelExternalIdExtractionXPathRuleTest {

    @Test
    public void test() {
        List<String> bazelQueryCommandArgsIncludingQuery = Arrays.asList("not used by this test");
        BazelExternalIdExtractionSimpleRule simpleRule = new BazelExternalIdExtractionSimpleRule(bazelQueryCommandArgsIncludingQuery, "test_class",
            "testRuleElementSelectorValue", "testArtifactStringSeparatorRegex");
        BazelExternalIdExtractionXPathRule xPathRule = new BazelExternalIdExtractionXPathRule(simpleRule);
        assertEquals(1, xPathRule.getBazelQueryCommandArgsIncludingQuery().size());
        assertEquals("not used by this test", xPathRule.getBazelQueryCommandArgsIncludingQuery().get(0));
        assertEquals("/query/rule[@class='test_class']/string[@name='testRuleElementSelectorValue']", xPathRule.getxPathQuery());
        assertEquals("value", xPathRule.getRuleElementValueAttrName());
        assertEquals("testArtifactStringSeparatorRegex", xPathRule.getArtifactStringSeparatorRegex());
    }
}
