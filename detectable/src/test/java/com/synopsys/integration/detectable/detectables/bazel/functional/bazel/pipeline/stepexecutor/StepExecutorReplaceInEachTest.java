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
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline.stepexecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.stepexecutor.StepExecutorReplaceInEach;
import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorReplaceInEachTest {

    // The first set of tests uses the input we originally expected

    @Test
    public void testRemoveLeadingAtSign() throws IntegrationException {
        final List<String> input = Arrays.asList("@org_apache_commons_commons_io//jar:jar", "@com_google_guava_guava//jar:jar");
        final StepExecutor stepExecutor = new StepExecutorReplaceInEach("^@", "");
        final List<String> output = stepExecutor.process(input);
        assertEquals(2, output.size());
        assertEquals("org_apache_commons_commons_io//jar:jar", output.get(0));
        assertEquals("com_google_guava_guava//jar:jar", output.get(1));
    }

    @Test
    public void testRemoveTrailingJunk() throws IntegrationException {
        final List<String> input = Arrays.asList("org_apache_commons_commons_io//jar:jar", "com_google_guava_guava//jar:jar");
        final StepExecutor stepExecutor = new StepExecutorReplaceInEach("//.*", "");
        final List<String> output = stepExecutor.process(input);
        assertEquals(2, output.size());
        assertEquals("org_apache_commons_commons_io", output.get(0));
        assertEquals("com_google_guava_guava", output.get(1));
    }

    @Test
    public void testInsertPrefix() throws IntegrationException {
        final List<String> input = Arrays.asList("org_apache_commons_commons_io", "com_google_guava_guava");
        final StepExecutor stepExecutor = new StepExecutorReplaceInEach("^", "//external:");
        final List<String> output = stepExecutor.process(input);
        assertEquals(2, output.size());
        assertEquals("//external:org_apache_commons_commons_io", output.get(0));
        assertEquals("//external:com_google_guava_guava", output.get(1));
    }

    @Test
    public void testMavenInstallBuildOutputExtractMavenCoordinates() throws IntegrationException {
        final List<String> input = Arrays.asList("  tags = [\"maven_coordinates=com.google.guava:guava:27.0-jre\"],");
        final StepExecutor stepExecutorOne = new StepExecutorReplaceInEach("^\\s*tags\\s*\\s*=\\s*\\[\\s*\"maven_coordinates=", "");
        final List<String> stepOneOutput = stepExecutorOne.process(input);

        final StepExecutor stepExecutorTwo = new StepExecutorReplaceInEach("\".*", "");
        final List<String> output = stepExecutorTwo.process(stepOneOutput);

        assertEquals(1, output.size());
        assertEquals("com.google.guava:guava:27.0-jre", output.get(0));
    }

    // The next set of tests uses more complex input

    @Test
    public void testRemoveLeadingAtSignMixedTags() throws IntegrationException {
        final List<String> input = Arrays.asList("  tags = [\"__SOME_OTHER_TAG__\", \"maven_coordinates=com.company.thing:thing-common-client:2.100.0\"],", "  tags = [\"maven_coordinates=com.google.code.findbugs:jsr305:3.0.2\"],");
        final StepExecutor stepExecutor1 = new StepExecutorReplaceInEach(".*\"maven_coordinates=", "");
        final StepExecutor stepExecutor2 = new StepExecutorReplaceInEach("\".*", "");
        final List<String> intermediate = stepExecutor1.process(input);
        final List<String> output = stepExecutor2.process(intermediate);

        assertEquals(2, output.size());
        assertEquals("com.company.thing:thing-common-client:2.100.0", output.get(0));
        assertEquals("com.google.code.findbugs:jsr305:3.0.2", output.get(1));
    }
}
