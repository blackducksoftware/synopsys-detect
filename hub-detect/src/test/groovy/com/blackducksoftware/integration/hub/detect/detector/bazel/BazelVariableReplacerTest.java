package com.blackducksoftware.integration.hub.detect.detector.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BazelVariableReplacerTest {

    @Test
    public void test() {
        BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("testBazelTarget", "testBazelTargetDependencyId");
        final List<String> origArgs = new ArrayList<>();
        origArgs.add("query");
        origArgs.add("filter(\"@.*:jar\", deps(XXXdetect.bazel.targetXXX))");

        final List<String> adjustedArgs = substitutor.substitute(origArgs);
        assertEquals(2, adjustedArgs.size());
        assertEquals("query", adjustedArgs.get(0));
        assertEquals("filter(\"@.*:jar\", deps(testBazelTarget))", adjustedArgs.get(1));
    }
}
