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

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;
import com.synopsys.integration.detectable.detectables.bazel.BazelWorkspace;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;

public class BazelWorkspaceTest {

    @Test
    public void testSingleRule() {
        File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE");
        BazelWorkspace bazelWorkspace = new BazelWorkspace(workspaceFile);

        assertEquals(Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL), bazelWorkspace.getDependencyRules());
    }

    @Test
    public void testMultipleRules() {
        File workspaceFile = new File("src/test/resources/detectables/functional/bazel/WORKSPACE_multipleRules");
        BazelWorkspace bazelWorkspace = new BazelWorkspace(workspaceFile);

        Set<WorkspaceRule> rulesFound = bazelWorkspace.getDependencyRules();
        assertEquals(Sets.newHashSet(WorkspaceRule.MAVEN_INSTALL, WorkspaceRule.HASKELL_CABAL_LIBRARY), rulesFound);
    }
}
