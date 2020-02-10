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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileParser;

class GitFileParserTest {
    @Test
    void parseHeadFile() throws IOException {
        final GitFileParser gitFileParser = new GitFileParser();
        final String gitHeadContent = "ref: refs/heads/master\n";

        final InputStream inputStream = IOUtils.toInputStream(gitHeadContent, StandardCharsets.UTF_8);
        final String head = gitFileParser.parseGitHead(inputStream);

        Assertions.assertEquals("refs/heads/master", head);
    }

    @Test
    void parseGitConfig() throws IOException {
        final GitFileParser gitFileParser = new GitFileParser();
        final String gitConfigContent = "[core]\n"
                                            + "\trepositoryformatversion = 0\n"
                                            + "\tfilemode = true\n"
                                            + "\tbare = false\n"
                                            + "\tlogallrefupdates = true\n"
                                            + "\tignorecase = true\n"
                                            + "\tprecomposeunicode = true\n"
                                            + "[remote \"origin\"]\n"
                                            + "\turl = https://github.com/blackducksoftware/synopsys-detect.git\n"
                                            + "\tfetch = +refs/heads/*:refs/remotes/origin/*\n"
                                            + "[branch \"master\"]\n"
                                            + "\tremote = origin\n"
                                            + "\tmerge = refs/heads/master\n"
                                            + "[branch \"master-backup\"]\n"
                                            + "\tremote = origin\n"
                                            + "\tmerge = refs/heads/master-backup\n"
                                            + "[branch \"6.0.0-actual\"]\n"
                                            + "\tremote = origin\n"
                                            + "\tmerge = refs/heads/6.0.0-actual\n"
                                            + "[branch \"deployment-test\"]\n"
                                            + "\tremote = origin\n"
                                            + "\tmerge = refs/heads/deployment-test\n"
                                            + "[branch \"verify-config\"]\n"
                                            + "\tremote = origin\n"
                                            + "\tmerge = refs/heads/verify-config\n";

        final InputStream inputStream = IOUtils.toInputStream(gitConfigContent, StandardCharsets.UTF_8);
        final List<GitConfigElement> gitConfigElements = gitFileParser.parseGitConfig(inputStream);

        Assertions.assertEquals(7, gitConfigElements.size());

        final List<GitConfigElement> gitConfigCores = gitConfigElements.stream()
                                                          .filter(gitConfigElement -> gitConfigElement.getElementType().equals("core"))
                                                          .collect(Collectors.toList());
        Assertions.assertEquals(1, gitConfigCores.size());

        final List<GitConfigElement> gitConfigRemotes = gitConfigElements.stream()
                                                            .filter(gitConfigElement -> gitConfigElement.getElementType().equals("remote"))
                                                            .collect(Collectors.toList());
        Assertions.assertEquals(1, gitConfigRemotes.size());

        final List<GitConfigElement> gitConfigBranches = gitConfigElements.stream()
                                                             .filter(gitConfigElement -> gitConfigElement.getElementType().equals("branch"))
                                                             .collect(Collectors.toList());
        Assertions.assertEquals(5, gitConfigBranches.size());

        final Optional<GitConfigElement> remoteOrigin = gitConfigElements.stream()
                                                            .filter(gitConfigElement -> gitConfigElement.getElementType().equals("remote"))
                                                            .filter(gitConfigElement -> gitConfigElement.getName().isPresent())
                                                            .filter(gitConfigElement -> gitConfigElement.getName().get().equals("origin"))
                                                            .findAny();
        Assertions.assertTrue(remoteOrigin.isPresent());
        Assertions.assertTrue(remoteOrigin.get().containsKey("fetch"));

        final String fetch = remoteOrigin.get().getProperty("fetch");
        Assertions.assertEquals("+refs/heads/*:refs/remotes/origin/*", fetch);
    }
}