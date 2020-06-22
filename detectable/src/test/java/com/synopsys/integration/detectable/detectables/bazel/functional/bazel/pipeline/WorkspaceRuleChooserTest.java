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
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.exception.IntegrationException;

public class WorkspaceRuleChooserTest {

    private static final Set<WorkspaceRule> parsedWorkspaceRulesJustMavenInstall = Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL);
    private static final Set<WorkspaceRule> parsedWorkspaceRulesAll = Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL,
        WorkspaceRule.HASKELL_CABAL_LIBRARY, WorkspaceRule.MAVEN_JAR);

    @Test
    public void testDerivedBazelDependencyRule() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(null, parsedWorkspaceRulesJustMavenInstall);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testDerivedBazelDependencyRulesAll() throws IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(null, parsedWorkspaceRulesAll);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testProvidedBazelDependencyRule() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(WorkspaceRule.MAVEN_INSTALL), parsedWorkspaceRulesJustMavenInstall);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testOverriddenBazelDependencyRule() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(WorkspaceRule.MAVEN_JAR), parsedWorkspaceRulesJustMavenInstall);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_jar", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testMultipleRules() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(WorkspaceRule.MAVEN_JAR, WorkspaceRule.MAVEN_INSTALL, WorkspaceRule.HASKELL_CABAL_LIBRARY), parsedWorkspaceRulesJustMavenInstall);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    @Test
    public void testOverriddenWithUnspecified() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(WorkspaceRule.UNSPECIFIED), parsedWorkspaceRulesJustMavenInstall);
        assertEquals(1, chosenWorkspaceRules.size());
        assertEquals("maven_install", chosenWorkspaceRules.iterator().next().getName());
    }

    @Test
    public void testOverriddenWithUnspecifiedAndSpecified() throws IOException, IntegrationException {
        Set<WorkspaceRule> chosenWorkspaceRules = run(Arrays.asList(WorkspaceRule.MAVEN_JAR, WorkspaceRule.UNSPECIFIED, WorkspaceRule.MAVEN_INSTALL, WorkspaceRule.HASKELL_CABAL_LIBRARY), parsedWorkspaceRulesJustMavenInstall);
        assertEquals(3, chosenWorkspaceRules.size());
    }

    private Set<WorkspaceRule> run(List<WorkspaceRule> providedBazelDependencyRule, Set<WorkspaceRule> parsedWorkspaceRules) throws IntegrationException {
        WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        Set<WorkspaceRule> chosenWorkspaceRules = workspaceRuleChooser.choose(parsedWorkspaceRules, providedBazelDependencyRule);
        return chosenWorkspaceRules;
    }

    // TODO test list of given rules, and list of found rules, and mixed combos
    // and user requests UNSPECIFIED
}
