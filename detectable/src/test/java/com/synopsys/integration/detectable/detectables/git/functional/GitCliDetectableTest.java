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
package com.synopsys.integration.detectable.detectables.git.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.executable.resolver.GitResolver;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.executable.ExecutableOutput;

public class GitCliDetectableTest extends DetectableFunctionalTest {
    public GitCliDetectableTest() throws IOException {
        super("git-cli");
    }

    @Override
    public void setup() throws IOException {
        addDirectory(Paths.get(".git"));

        final String gitRemoteUrlOutput = "https://github.com/blackducksoftware/synopsys-detect";
        final ExecutableOutput gitConfigExecutableOutput = new ExecutableOutput(0, gitRemoteUrlOutput, "");
        addExecutableOutput(gitConfigExecutableOutput, "git", "config", "--get", "remote.origin.url");

        final String gitBranchOutput = "branch-version";
        final ExecutableOutput gitBranchExecutableOutput = new ExecutableOutput(0, gitBranchOutput, "");
        addExecutableOutput(gitBranchExecutableOutput, "git", "rev-parse", "--abbrev-ref", "HEAD");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment environment) {
        class GitExeResolver implements GitResolver {
            @Override
            public ExecutableTarget resolveGit() {
                return ExecutableTarget.forCommand("git");
            }
        }

        return detectableFactory.createGitCliDetectable(environment, new GitExeResolver());
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.getProjectName());
        Assertions.assertEquals("branch-version", extraction.getProjectVersion());
    }
}