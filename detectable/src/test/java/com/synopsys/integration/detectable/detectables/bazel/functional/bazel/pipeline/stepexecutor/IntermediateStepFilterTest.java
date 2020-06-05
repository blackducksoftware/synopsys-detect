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

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepFilter;
import com.synopsys.integration.exception.IntegrationException;

public class IntermediateStepFilterTest {

    private static final String NAME_LINE = "  name = \"com_google_code_findbugs_jsr305\",";
    private static final String TAGS_LINE_MAVEN_COORDINATES = "  tags = [\"maven_coordinates=com.google.code.findbugs:jsr305:3.0.2\"],";
    private static final String TAGS_LINE_OTHER = "  tags = [\"__SOME_OTHER_TAG__\"],";
    private static final String TAGS_LINE_MIXED = "  tags = [\"__SOME_OTHER_TAG__\", \"maven_coordinates=com.company.thing:thing-common-client:2.100.0\"],";

    @Test
    public void testMavenCoordinateOnly() throws IntegrationException {
        final IntermediateStep intermediateStep = new IntermediateStepFilter(".*maven_coordinates=.*");

        final List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE_MAVEN_COORDINATES);
        final List<String> output = intermediateStep.process(input);
        assertEquals(1, output.size());
        assertEquals(TAGS_LINE_MAVEN_COORDINATES, output.get(0));
    }

    @Test
    public void testOtherTagType() throws IntegrationException {
        final IntermediateStep intermediateStep = new IntermediateStepFilter(".*maven_coordinates=.*");

        final List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE_OTHER);
        final List<String> output = intermediateStep.process(input);
        assertEquals(0, output.size());
    }

    @Test
    public void testMixed() throws IntegrationException {
        final IntermediateStep intermediateStep = new IntermediateStepFilter(".*maven_coordinates=.*");

        final List<String> input = Arrays.asList(NAME_LINE, TAGS_LINE_MIXED);
        final List<String> output = intermediateStep.process(input);
        assertEquals(1, output.size());
        assertEquals(TAGS_LINE_MIXED, output.get(0));
    }
}
