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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;

public class GitParseDetectableTest extends DetectableFunctionalTest {
    public GitParseDetectableTest() throws IOException {
        super("git-parse");
    }

    @Override
    public void setup() throws IOException {
        Path gitDirectory = addDirectory(Paths.get(".git"));
        addFile(gitDirectory.resolve("config"),
            "[core]",
            "	repositoryformatversion = 0",
            "	filemode = true",
            "	bare = false",
            "	logallrefupdates = true",
            "	ignorecase = true",
            "	precomposeunicode = true",
            "[remote \"origin\"]",
            "	url = https://github.com/blackducksoftware/synopsys-detect.git",
            "	fetch = +refs/heads/*:refs/remotes/origin/*",
            "[branch \"master\"]",
            "	remote = origin",
            "	merge = refs/heads/master",
            "[branch \"test\"]",
            "	remote = origin",
            "	merge = refs/heads/test"
        );

        addFile(gitDirectory.resolve("HEAD"), Collections.singletonList("ref: refs/heads/master\n"));
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createGitParseDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
        Assertions.assertEquals("blackducksoftware/synopsys-detect", extraction.getProjectName());
        Assertions.assertEquals("master", extraction.getProjectVersion());
    }
}