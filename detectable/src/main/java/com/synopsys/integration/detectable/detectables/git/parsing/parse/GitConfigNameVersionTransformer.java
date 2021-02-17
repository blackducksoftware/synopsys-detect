/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigBranch;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigRemote;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class GitConfigNameVersionTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GitUrlParser gitUrlParser;

    public GitConfigNameVersionTransformer(final GitUrlParser gitUrlParser) {
        this.gitUrlParser = gitUrlParser;
    }

    public NameVersion transformToProjectInfo(final GitConfig gitConfig, final String gitHead) throws IntegrationException, MalformedURLException {
        final Optional<GitConfigBranch> currentBranch = gitConfig.getGitConfigBranches().stream()
                                                            .filter(it -> it.getMerge().equalsIgnoreCase(gitHead))
                                                            .findFirst();

        final String projectName;
        final String projectVersionName;
        if (currentBranch.isPresent()) {
            logger.debug(String.format("Parsing a git repository on branch '%s'.", currentBranch.get().getName()));

            final String remoteName = currentBranch.get().getRemoteName();
            final String remoteUrl = gitConfig.getGitConfigRemotes().stream()
                                         .filter(it -> it.getName().equals(remoteName))
                                         .map(GitConfigRemote::getUrl)
                                         .findFirst()
                                         .orElseThrow(() -> new IntegrationException(String.format("Failed to find a url for remote '%s'.", remoteName)));

            projectName = gitUrlParser.getRepoName(remoteUrl);
            projectVersionName = currentBranch.get().getName();
        } else {
            logger.debug(String.format("Parsing a git repository with detached head '%s'.", gitHead));

            final String remoteUrl = gitConfig.getGitConfigRemotes().stream()
                                         .findFirst()
                                         .map(GitConfigRemote::getUrl)
                                         .orElseThrow(() -> new IntegrationException("No remote urls were found in config."));

            projectName = gitUrlParser.getRepoName(remoteUrl);
            projectVersionName = gitHead;
        }

        return new NameVersion(projectName, projectVersionName);
    }
}
