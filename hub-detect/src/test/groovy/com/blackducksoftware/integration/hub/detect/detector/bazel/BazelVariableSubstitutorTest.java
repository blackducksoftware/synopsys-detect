package com.blackducksoftware.integration.hub.detect.detector.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BazelVariableSubstitutorTest {

    @Test
    public void test() {
        BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("//foo:foolib", "//external:org_apache_commons_commons_io");
        final List<String> origArgs = new ArrayList<>();
        origArgs.add("query");
        origArgs.add("filter(\"@.*:jar\", deps(${detect.bazel.target}))");
        origArgs.add("kind(maven_jar, ${detect.bazel.target.dependency})");

        final List<String> adjustedArgs = substitutor.substitute(origArgs);
        assertEquals(3, adjustedArgs.size());
        assertEquals("query", adjustedArgs.get(0));
        assertEquals("filter(\"@.*:jar\", deps(//foo:foolib))", adjustedArgs.get(1));
        assertEquals("kind(maven_jar, //external:org_apache_commons_commons_io)", adjustedArgs.get(2));
    }
}
