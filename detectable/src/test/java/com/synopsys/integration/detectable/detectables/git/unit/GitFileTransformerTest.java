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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.detectable.detectables.git.parsing.parse.GitFileTransformer;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

class GitFileTransformerTest {

    @Test
    void transform() throws MalformedURLException, IntegrationException {
        final Map<String, String> remoteProperties = new HashMap<>();
        remoteProperties.put("url", "https://github.com/blackducksoftware/blackduck-artifactory.git");
        remoteProperties.put("fetch", "+refs/heads/*:refs/remotes/origin/");
        final GitConfigElement remote = new GitConfigElement("remote", "origin", remoteProperties);

        final Map<String, String> branchProperties = new HashMap<>();
        branchProperties.put("remote", "origin");
        branchProperties.put("merge", "refs/heads/master");
        final GitConfigElement branch = new GitConfigElement("branch", "master", branchProperties);

        final Map<String, String> badBranchProperties = new HashMap<>();
        badBranchProperties.put("remote", "origin");
        badBranchProperties.put("merge", "refs/heads/bad-branch");
        final GitConfigElement badBranch = new GitConfigElement("branch", "bad-branch", badBranchProperties);

        final String gitHead = "refs/heads/master";
        final List<GitConfigElement> gitConfigElements = new ArrayList<>();
        gitConfigElements.add(remote);
        gitConfigElements.add(branch);
        gitConfigElements.add(badBranch);

        final GitUrlParser gitUrlParser = new GitUrlParser();
        final GitFileTransformer gitFileTransformer = new GitFileTransformer(gitUrlParser);
        final NameVersion nameVersion = gitFileTransformer.transformGitConfigElements(gitConfigElements, gitHead);

        Assertions.assertEquals("blackducksoftware/blackduck-artifactory", nameVersion.getName());
        Assertions.assertEquals("master", nameVersion.getVersion());
    }
}