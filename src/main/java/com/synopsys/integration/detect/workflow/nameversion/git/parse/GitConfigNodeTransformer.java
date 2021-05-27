/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git.parse;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.nameversion.git.model.GitConfig;
import com.synopsys.integration.detect.workflow.nameversion.git.model.GitConfigBranch;
import com.synopsys.integration.detect.workflow.nameversion.git.model.GitConfigNode;
import com.synopsys.integration.detect.workflow.nameversion.git.model.GitConfigRemote;

public class GitConfigNodeTransformer {
    public GitConfig createGitConfig(List<GitConfigNode> gitConfigNodes) {
        List<GitConfigRemote> gitConfigRemotes = gitConfigNodes.stream()
                                                     .filter(node -> "remote".equals(node.getType()))
                                                     .map(node -> {
                                                         String remoteNodeName = node.getName().orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a name."));
                                                         String remoteNodeUrl = node.getProperty("url").orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a url field."));
                                                         String remoteNodeFetch = node.getProperty("fetch").orElseThrow(() -> new IllegalArgumentException("Expected remote node to have a fetch field."));
                                                         return new GitConfigRemote(remoteNodeName, remoteNodeUrl, remoteNodeFetch);
                                                     })
                                                     .collect(Collectors.toList());

        List<GitConfigBranch> gitConfigBranches = gitConfigNodes.stream()
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
