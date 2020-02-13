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
package com.synopsys.integration.detectable.detectables.git.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;

class GitFileParserTest {
    @Test
    public void parseHeadFile() {
        final GitFileParser gitFileParser = new GitFileParser();
        final String gitHeadContent = "ref: refs/heads/master\n";
        final String head = gitFileParser.parseGitHead(gitHeadContent);
        Assertions.assertEquals("refs/heads/master", head);
    }

    @Test
    public void parseGitConfig() {
        final GitFileParser gitFileParser = new GitFileParser();
        final List<String> output = Arrays.asList(
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

        final List<GitConfigElement> gitConfigElements = gitFileParser.parseGitConfig(output);
        Assertions.assertEquals(4, gitConfigElements.size());

        final List<GitConfigElement> gitConfigCores = getElements(gitConfigElements, "core");
        Assertions.assertEquals(1, gitConfigCores.size());

        final List<GitConfigElement> gitConfigRemotes = getElements(gitConfigElements, "remote");
        Assertions.assertEquals(1, gitConfigRemotes.size());

        final List<GitConfigElement> gitConfigBranches = getElements(gitConfigElements, "branch");
        Assertions.assertEquals(2, gitConfigBranches.size());

        final Optional<GitConfigElement> remoteOrigin = gitConfigRemotes.stream()
                                                            .filter(it -> it.getName().isPresent())
                                                            .filter(it -> it.getName().get().equals("origin"))
                                                            .findAny();

        Assertions.assertTrue(remoteOrigin.isPresent());
        Assertions.assertTrue(remoteOrigin.get().containsKey("fetch"));

        final String fetch = remoteOrigin.get().getProperty("fetch");
        Assertions.assertEquals("+refs/heads/*:refs/remotes/origin/*", fetch);
    }

    @NotNull
    private List<GitConfigElement> getElements(@NotNull final List<GitConfigElement> gitConfigElements, @NotNull final String tag) {
        return gitConfigElements.stream()
                   .filter(gitConfigElement -> tag.equals(gitConfigElement.getElementType()))
                   .collect(Collectors.toList());
    }
}