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
package com.synopsys.integration.detectable.detectables.git.parsing.parse;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class GitFileTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GitUrlParser gitUrlParser;

    public GitFileTransformer(final GitUrlParser gitUrlParser) {
        this.gitUrlParser = gitUrlParser;
    }

    public NameVersion transformGitConfigElements(final List<GitConfigElement> gitConfigElements, final String gitHead) throws IntegrationException, MalformedURLException {
        final Optional<GitConfigElement> currentBranch = gitConfigElements.stream()
                                                             .filter(gitConfigElement -> gitConfigElement.getElementType().equals("branch"))
                                                             .filter(gitConfigElement -> gitConfigElement.containsKey("merge"))
                                                             .filter(gitConfigElement -> gitConfigElement.getProperty("merge").equalsIgnoreCase(gitHead))
                                                             .filter(gitConfigElement -> gitConfigElement.containsKey("remote"))
                                                             .findFirst();

        final Optional<String> currentBranchRemoteName = currentBranch
                                                             .map(gitConfigElement -> gitConfigElement.getProperty("remote"));

        final String projectName;
        final String projectVersionName;
        if (currentBranchRemoteName.isPresent()) {
            logger.debug(String.format("Parsing a git repository on branch '%s'.", currentBranchRemoteName.get()));

            final String remoteUrl = gitConfigElements.stream()
                                         .filter(gitConfigElement -> gitConfigElement.getElementType().equals("remote"))
                                         .filter(gitConfigElement -> gitConfigElement.getName().isPresent())
                                         .filter(gitConfigElement -> gitConfigElement.getName().get().equals(currentBranchRemoteName.get()))
                                         .filter(gitConfigElement -> gitConfigElement.containsKey("url"))
                                         .map(gitConfigElement -> gitConfigElement.getProperty("url"))
                                         .findFirst()
                                         .orElseThrow(() -> new IntegrationException("Failed to find a remote url."));

            projectName = gitUrlParser.getRepoName(remoteUrl);
            projectVersionName = currentBranch.get().getName().orElse(null);
        } else {
            logger.debug(String.format("Parsing a git repository with detached head '%s'.", gitHead));

            final String remoteUrl = gitConfigElements.stream()
                                         .filter(gitConfigElement -> gitConfigElement.getElementType().equals("remote"))
                                         .filter(gitConfigElement -> gitConfigElement.containsKey("url"))
                                         .map(gitConfigElement -> gitConfigElement.getProperty("url"))
                                         .findFirst()
                                         .orElseThrow(() -> new IntegrationException("No named remotes were found in config."));

            projectName = gitUrlParser.getRepoName(remoteUrl);
            projectVersionName = gitHead;
        }

        return new NameVersion(projectName, projectVersionName);
    }
}
