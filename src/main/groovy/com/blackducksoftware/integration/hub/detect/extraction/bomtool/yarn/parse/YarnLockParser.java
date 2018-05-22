/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer;
import com.blackducksoftware.integration.hub.detect.nameversion.builder.LinkedNameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.builder.NameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.LinkMetadata;

@Component
public class YarnLockParser extends BaseYarnParser {

    @Autowired
    private NameVersionNodeTransformer nameVersionNodeTransformer;

    public DependencyGraph parseYarnLock(final List<String> yarnLockText) {
        NameVersionNode rootNode = new NameVersionNode();
        rootNode.setName(String.format("detectRootNode - %s", UUID.randomUUID()));
        LinkedNameVersionNodeBuilder nameVersionLinkNodeBuilder = new LinkedNameVersionNodeBuilder(rootNode);

        NameVersionNode currentNode = null;
        boolean dependenciesStarted = false;
        for (String line : yarnLockText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("#")) {
                continue;
            }

            int level = getLineLevel(line);
            if (level == 0) {
                currentNode = lineToNameVersionNode(nameVersionLinkNodeBuilder, rootNode, trimmedLine);
                dependenciesStarted = false;
                continue;
            }

            if (level == 1 && trimmedLine.startsWith("version")) {
                String fieldName = trimmedLine.split(" ")[0];
                currentNode.setVersion(trimmedLine.substring(fieldName.length()).replaceAll("\"", "").trim());
                continue;
            }

            if (level == 1 && trimmedLine.equals("dependencies:")) {
                dependenciesStarted = true;
                continue;
            }

            if (level == 2 && dependenciesStarted) {
                NameVersionNode dependency = dependencyLineToNameVersionNode(line);
                nameVersionLinkNodeBuilder.addChildNodeToParent(dependency, currentNode);
                continue;
            }
        }

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        List<NameVersionNode> children = nameVersionLinkNodeBuilder.build().getChildren();
        for (NameVersionNode child : children) {
            Dependency root = nameVersionNodeTransformer.addNameVersionNodeToDependencyGraph(graph, Forge.NPM, child);
            graph.addChildToRoot(root);
        }

        return graph;
    }

    // Example: "mime-types@^2.1.12" becomes "mime-types"
    private String getNameFromFuzzyName(final String fuzzyName) {
        String cleanName = fuzzyName.replace("\"", "");
        String[] splitName = cleanName.split("@");
        String version = splitName[splitName.length - 1];
        String name = cleanName.substring(0, cleanName.indexOf(version) - 1).trim();

        return name;
    }

    private NameVersionNode dependencyLineToNameVersionNode(final String line) {
        final NameVersionNode nameVersionNode = new NameVersionNode();
        nameVersionNode.setName(line.trim().replaceFirst(" ", "@").replace("\"", ""));

        return nameVersionNode;
    }

    private NameVersionNode lineToNameVersionNode(final NameVersionNodeBuilder nameVersionNodeBuilder, final NameVersionNode root, final String line) {
        String cleanLine = line.replace("\"", "").replace(":", "");
        String[] splitLine = cleanLine.split(",");
        List<String> fuzzyNames = new ArrayList<>();
        for (String splitPart : splitLine) {
            fuzzyNames.add(splitPart.trim());
        }

        if (fuzzyNames.isEmpty()) {
            return null;
        }

        final NameVersionNode linkedNameVersionNode = new NameVersionNode();
        linkedNameVersionNode.setName(getNameFromFuzzyName(fuzzyNames.get(0)));

        for (String fuzzyName : fuzzyNames) {
            NameVersionNode nameVersionLinkNode = new NameVersionNode();
            nameVersionLinkNode.setName(fuzzyName);

            LinkMetadata linkMetadata = new LinkMetadata();
            linkMetadata.setLinkNode(linkedNameVersionNode);
            nameVersionLinkNode.setMetadata(linkMetadata);
            nameVersionNodeBuilder.addChildNodeToParent(nameVersionLinkNode, root);
        }

        return linkedNameVersionNode;
    }

}
