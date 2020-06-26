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
package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.exception.IntegrationException;

public class WorkspaceRuleChooserTest {

    private static final Set<WorkspaceRule> PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL = Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL);
    private static final Set<WorkspaceRule> PARSED_WORKSPACE_RULES_THREE = Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL,
        WorkspaceRule.HASKELL_CABAL_LIBRARY, WorkspaceRule.MAVEN_JAR);

    @Test
    public void testOneRuleParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(null, PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testThreeRulesParsed() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(null, PARSED_WORKSPACE_RULES_THREE);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testOneProvidedSameOneParsed() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_INSTALL)), PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testOneRuleProvidedDifferentOneParsed() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_JAR)), PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_jar", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testThreeProvidedOneParsed() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_JAR), FilterableEnumValue.value(WorkspaceRule.MAVEN_INSTALL), FilterableEnumValue.value(WorkspaceRule.HASKELL_CABAL_LIBRARY)),
            PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testAllValueProvidedOneParsed() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.value(WorkspaceRule.MAVEN_JAR), FilterableEnumValue.allValue(), FilterableEnumValue.value(WorkspaceRule.HASKELL_CABAL_LIBRARY)),
            PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testNoneValueProvidedOneParsed() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(FilterableEnumValue.noneValue()),
            PARSED_WORKSPACE_RULES_JUST_MAVEN_INSTALL);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    private Set<WorkspaceRule> run(List<FilterableEnumValue<WorkspaceRule>> providedBazelDependencyRule, Set<WorkspaceRule> parsedWorkspaceRules) throws IntegrationException {
        WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        Set<WorkspaceRule> chosenWorkspaceRules = workspaceRuleChooser.choose(parsedWorkspaceRules, providedBazelDependencyRule);
        return chosenWorkspaceRules;
    }
}
