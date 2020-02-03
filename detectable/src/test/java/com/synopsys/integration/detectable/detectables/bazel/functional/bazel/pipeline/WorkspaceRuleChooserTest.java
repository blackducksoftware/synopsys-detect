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

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.WorkspaceRuleChooser;
import com.synopsys.integration.exception.IntegrationException;

public class WorkspaceRuleChooserTest {

    @Test
    public void testDerivedBazelDependencyRule() throws IntegrationException {
        final WorkspaceRule chosenWorkspaceRule = run(null);
        assertEquals("maven_install", chosenWorkspaceRule.getName());
    }

    @Test
    public void testProvidedBazelDependencyRule() throws IOException, IntegrationException {
        final WorkspaceRule chosenWorkspaceRule = run("maven_install");
        assertEquals("maven_install", chosenWorkspaceRule.getName());
    }

    @Test
    public void testOverriddenBazelDependencyRule() throws IOException, IntegrationException {
        final WorkspaceRule chosenWorkspaceRule = run("maven_jar");
        assertEquals("maven_jar", chosenWorkspaceRule.getName());
    }

    private WorkspaceRule run(final String providedBazelDependencyRule) throws IntegrationException {
        final WorkspaceRuleChooser workspaceRuleChooser = new WorkspaceRuleChooser();
        final WorkspaceRule chosenWorkspaceRule = workspaceRuleChooser.choose(WorkspaceRule.MAVEN_INSTALL, providedBazelDependencyRule);
        return chosenWorkspaceRule;
    }
}
