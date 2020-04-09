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

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigBranch;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigRemote;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitConfigNameVersionTransformer;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

class GitConfigNameVersionTransformerTest {

    @Test
    void transform() throws MalformedURLException, IntegrationException {
        final GitConfigRemote gitConfigRemote = new GitConfigRemote("origin", "https://github.com/blackducksoftware/blackduck-artifactory.git", "+refs/heads/*:refs/remotes/origin/");
        final GitConfigBranch gitConfigBranch = new GitConfigBranch("master", "origin", "refs/heads/master");
        final GitConfigBranch badBranch = new GitConfigBranch("bad-branch", "origin", "refs/heads/bad-branch");
        final List<GitConfigRemote> gitConfigRemotes = Collections.singletonList(gitConfigRemote);
        final List<GitConfigBranch> gitConfigBranches = Arrays.asList(gitConfigBranch, badBranch);

        final GitConfig gitConfig = new GitConfig(gitConfigRemotes, gitConfigBranches);
        final String gitHead = "refs/heads/master";

        final GitUrlParser gitUrlParser = new GitUrlParser();
        final GitConfigNameVersionTransformer gitConfigNameVersionTransformer = new GitConfigNameVersionTransformer(gitUrlParser);
        final NameVersion nameVersion = gitConfigNameVersionTransformer.transformToProjectInfo(gitConfig, gitHead);

        Assertions.assertEquals("blackducksoftware/blackduck-artifactory", nameVersion.getName());
        Assertions.assertEquals("master", nameVersion.getVersion());
    }

    /**
     * When we encounter a git repository with a detached head, the HEAD file will contain a commit hash instead of a reference to a git branch since it is detached.
     * In this case we want the version to be the commit hash as the version since a branch cannot be chosen.
     */
    @Test
    void transformDetachedHead() throws MalformedURLException, IntegrationException {
        final GitConfigRemote gitConfigRemote = new GitConfigRemote("origin", "https://github.com/blackducksoftware/synopsys-detect.git", "+refs/heads/*:refs/remotes/origin/");
        final GitConfigBranch gitConfigBranch = new GitConfigBranch("master", "origin", "refs/heads/master");
        final List<GitConfigRemote> gitConfigRemotes = Collections.singletonList(gitConfigRemote);
        final List<GitConfigBranch> gitConfigBranches = Collections.singletonList(gitConfigBranch);

        final GitConfig gitConfig = new GitConfig(gitConfigRemotes, gitConfigBranches);
        final String gitHead = "9ec2a2bcfa8651b6e096b06d72b1b9290b429e3c";

        final GitUrlParser gitUrlParser = new GitUrlParser();
        final GitConfigNameVersionTransformer gitConfigNameVersionTransformer = new GitConfigNameVersionTransformer(gitUrlParser);
        final NameVersion nameVersion = gitConfigNameVersionTransformer.transformToProjectInfo(gitConfig, gitHead);

        Assertions.assertEquals("blackducksoftware/synopsys-detect", nameVersion.getName());
        Assertions.assertEquals("9ec2a2bcfa8651b6e096b06d72b1b9290b429e3c", nameVersion.getVersion());
    }
}