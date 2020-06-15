/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;

public class BazelVariableSubstitutorTest {

    @Test
    public void testTargetOnly() {
        final BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("//foo:foolib", new ArrayList<>(0));
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
        final BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("//foo:foolib", null);
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

    @Test
    public void testListInsertion() {
        final BazelVariableSubstitutor substitutor = new BazelVariableSubstitutor("//foo:foolib", Arrays.asList("--define=a=b", "--define=c=d"));
        final List<String> origArgs = new ArrayList<>();
        origArgs.add("cquery");
        origArgs.add("${detect.bazel.cquery.options}");
        origArgs.add("filter(\"@.*:jar\", deps(${detect.bazel.target}))");
        origArgs.add("kind(maven_jar, ${input.item})");

        final List<String> adjustedArgs = substitutor.substitute(origArgs, "//external:org_apache_commons_commons_io");
        assertEquals(5, adjustedArgs.size());
        assertEquals("cquery", adjustedArgs.get(0));
        assertEquals("--define=a=b", adjustedArgs.get(1));
        assertEquals("--define=c=d", adjustedArgs.get(2));
        assertEquals("filter(\"@.*:jar\", deps(//foo:foolib))", adjustedArgs.get(3));
        assertEquals("kind(maven_jar, //external:org_apache_commons_commons_io)", adjustedArgs.get(4));
    }
}
