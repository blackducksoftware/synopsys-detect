///**
// * detectable
// *
// * Copyright (c) 2020 Synopsys, Inc.
// *
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements. See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership. The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License. You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied. See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package com.synopsys.integration.detect.workflow.nameversion.git.functional;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.Assertions;
//
//import com.synopsys.integration.detectable.Detectable;
//import com.synopsys.integration.detectable.DetectableEnvironment;
//import com.synopsys.integration.detectable.ExecutableTarget;
//import com.synopsys.integration.detectable.extraction.Extraction;
//import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
//import com.synopsys.integration.detectable.util.ExecutableOutputUtil;
//
//public class GitCliCommitHashDetectableTest extends DetectableFunctionalTest {
//    public GitCliCommitHashDetectableTest() throws IOException {
//        super("git-cli");
//    }
//
//    @Override
//    public void setup() throws IOException {
//        addDirectory(Paths.get(".git"));
//
//        addExecutableOutput(ExecutableOutputUtil.success("https://github.com/blackducksoftware/synopsys-detect"), "git", "config", "--get", "remote.origin.url");
//
//        addExecutableOutput(ExecutableOutputUtil.success("HEAD"), "git", "rev-parse", "--abbrev-ref", "HEAD");
//
//        addExecutableOutput(ExecutableOutputUtil.success("(HEAD -> develop, origin/develop, origin/HEAD)"), "git", "log", "-n", "1", "--pretty=%d", "HEAD");
//
//        addExecutableOutput(ExecutableOutputUtil.success("9ec2a2bcfa8651b6e096b06d72b1b9290b429e3c"), "git", "rev-parse", "HEAD");
//    }
//
//    @NotNull
//    @Override
//    public Detectable create(@NotNull DetectableEnvironment environment) {
//        return detectableFactory.createGitCliDetectable(environment, () -> ExecutableTarget.forCommand("git"));
//    }
//
//    @Override
//    public void assertExtraction(@NotNull Extraction extraction) {
//        Assertions.assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
//        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.getProjectName());
//        Assertions.assertEquals("9ec2a2bcfa8651b6e096b06d72b1b9290b429e3c", extraction.getProjectVersion());
//    }
//}