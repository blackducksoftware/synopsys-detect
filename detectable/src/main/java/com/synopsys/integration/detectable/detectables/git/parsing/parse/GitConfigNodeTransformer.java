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

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfig;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigBranch;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigNode;
import com.synopsys.integration.detectable.detectables.git.parsing.model.GitConfigRemote;

public class GitConfigNodeTransformer {
    public GitConfig createGitConfig(final List<GitConfigNode> gitConfigNodes) {
        final List<GitConfigRemote> gitConfigRemotes = gitConfigNodes.stream()
                                                           .filter(node -> "remote".equals(node.getType()))
                                                           .map(node -> {
                                                               String remoteNodeName = node.getName().orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a name."));
                                                               String remoteNodeUrl = node.getProperty("url").orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a url field."));
                                                               String remoteNodeFetch = node.getProperty("fetch").orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a fetch field."));
                                                               return new GitConfigRemote(remoteNodeName, remoteNodeUrl, remoteNodeFetch);
                                                           })
                                                           .collect(Collectors.toList());

        final List<GitConfigBranch> gitConfigBranches = gitConfigNodes.stream()
                                                            .filter(node -> "branch".equals(node.getType()))
                                                            .map(node -> {
                                                                String remoteNodeName = node.getName().orElseThrow(() -> new IllegalArgumentException("Expected branch node to have a name."));
                                                                String remoteNodeRemote = node.getProperty("remote").orElseThrow(() -> new IllegalArgumentException("Expected branch node to have a remote field."));
                                                                String remoteNodeMerge = node.getProperty("merge").orElseThrow(() -> new IllegalArgumentException("Expected branch node to have a fetch field."));
                                                                return new GitConfigBranch(remoteNodeName, remoteNodeRemote, remoteNodeMerge);
                                                            })
                                                            .collect(Collectors.toList());

        return new GitConfig(gitConfigRemotes, gitConfigBranches);
    }
}
