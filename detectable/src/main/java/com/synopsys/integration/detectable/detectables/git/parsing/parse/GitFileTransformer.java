/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigElement;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class GitFileTransformer {
    public NameVersion transformGitConfigElements(final List<GitConfigElement> gitConfigElements, final String gitHead) throws IntegrationException, MalformedURLException {
        final Optional<GitConfigElement> currentBranch = gitConfigElements.stream()
                                                             .filter(gitConfigElement -> gitConfigElement.getElementType().equals("branch"))
                                                             .filter(gitConfigElement -> gitConfigElement.containsKey("merge"))
                                                             .filter(gitConfigElement -> gitConfigElement.getProperty("merge").equalsIgnoreCase(gitHead))
                                                             .filter(gitConfigElement -> gitConfigElement.containsKey("remote"))
                                                             .findFirst();

        final Optional<String> currentBranchRemoteName = currentBranch
                                                             .map(gitConfigElement -> gitConfigElement.getProperty("remote"));

        if (!currentBranchRemoteName.isPresent()) {
            throw new IntegrationException(String.format("Failed to find a remote name for head %s", gitHead));
        }

        final Optional<String> remoteUrlOptional = gitConfigElements.stream()
                                                       .filter(gitConfigElement -> gitConfigElement.getElementType().equals("remote"))
                                                       .filter(gitConfigElement -> gitConfigElement.getName().isPresent())
                                                       .filter(gitConfigElement -> gitConfigElement.getName().get().equals(currentBranchRemoteName.get()))
                                                       .filter(gitConfigElement -> gitConfigElement.containsKey("url"))
                                                       .map(gitConfigElement -> gitConfigElement.getProperty("url"))
                                                       .findAny();

        if (!remoteUrlOptional.isPresent()) {
            throw new IntegrationException("Failed to find a remote url.");
        }

        final URL remoteURL = new URL(remoteUrlOptional.get());
        final String path = remoteURL.getPath();
        final String projectName = StringUtils.removeEnd(StringUtils.removeStart(path, "/"), ".git");
        final String projectVersionName = currentBranch.get().getName().orElse(null);

        return new NameVersion(projectName, projectVersionName);
    }
}
