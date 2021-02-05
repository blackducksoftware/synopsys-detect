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
package com.synopsys.integration.detectable.detectables.rebar.functional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class RebarDetectableTest extends DetectableFunctionalTest {
    public RebarDetectableTest() throws IOException {
        super("rebar");
    }

    @Override
    public void setup() throws IOException {
        addFile(Paths.get("rebar.config"));

        ExecutableOutput executableOutput = createStandardOutput("└─ project─1.0.0 (project app)",
            "   ├─ git_inner_parent_dependency─0.0.2 (git repo)",
            "   │  └─ hex_inner_child_dependency─0.3.0 (hex package)",
            "   └─ git_outer_parent_dependency─0.0.7 (git repo)",
            "      └─ git_outer_child_dependency─0.8.0 (git repo)");
        Map<String, String> environment = new HashMap<>();
        environment.put("REBAR_COLOR", "none");
        addExecutableOutput(executableOutput, environment, "rebar", "tree");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        class RebarResolver implements Rebar3Resolver {
            @Override
            public ExecutableTarget resolveRebar3() {
                return ExecutableTarget.forCommand("rebar");
            }
        }

        return detectableFactory.createRebarDetectable(environment, new RebarResolver());
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        Assertions.assertEquals("project", extraction.getProjectName(), "A rebar tree includes project info. Project name expected.");
        Assertions.assertEquals("1.0.0", extraction.getProjectVersion(), "A rebar tree includes project info. Project version name expected.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.HEX, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("git_inner_parent_dependency", "0.0.2");
        graphAssert.hasRootDependency("git_outer_parent_dependency", "0.0.7");

        graphAssert.hasParentChildRelationship("git_inner_parent_dependency", "0.0.2", "hex_inner_child_dependency", "0.3.0");
        graphAssert.hasParentChildRelationship("git_outer_parent_dependency", "0.0.7", "git_outer_child_dependency", "0.8.0");
    }
}
