package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.BazelVariableSubstitutor;

public class BazelVariableSubstitutorTest {

    @Test
    public void testTargetOnly() {
        BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("//foo:foolib");
        final List<String> origArgs = new ArrayList<>();
        origArgs.add("query");
        origArgs.add("filter(\"@.*:jar\", deps(${detect.bazel.target}))");

        final List<String> adjustedArgs = substitutor.substitute(origArgs, null);
        assertEquals(2, adjustedArgs.size());
        assertEquals("query", adjustedArgs.get(0));
        assertEquals("filter(\"@.*:jar\", deps(//foo:foolib))", adjustedArgs.get(1));
    }

    @Test
    public void testInput() {
        BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("//foo:foolib");
        final List<String> origArgs = new ArrayList<>();
        origArgs.add("query");
        origArgs.add("filter(\"@.*:jar\", deps(${detect.bazel.target}))");
        origArgs.add("kind(maven_jar, ${input.item})");

        final List<String> adjustedArgs = substitutor.substitute(origArgs, "//external:org_apache_commons_commons_io");
        assertEquals(3, adjustedArgs.size());
        assertEquals("query", adjustedArgs.get(0));
        assertEquals("filter(\"@.*:jar\", deps(//foo:foolib))", adjustedArgs.get(1));
        assertEquals("kind(maven_jar, //external:org_apache_commons_commons_io)", adjustedArgs.get(2));
    }
}
