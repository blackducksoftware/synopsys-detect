/**
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
package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.parse.IndentedTreeParser;

public class SbtPluginParser {
    private final IndentedTreeParser<SbtNode> indentedTreeParser;
    private final SbtPluginLineParser lineParser;
    private final ExternalIdFactory externalIdFactory;

    public SbtPluginParser(final IndentedTreeParser<SbtNode> indentedTreeParser, final SbtPluginLineParser lineParser, final ExternalIdFactory externalIdFactory) {
        this.indentedTreeParser = indentedTreeParser;
        this.lineParser = lineParser;
        this.externalIdFactory = externalIdFactory;
    }

    public List<DependencyGraph> parse(List<String> pluginOutput) {
        final List<SbtNode> nodes = pluginOutput.stream()
                                        .map(lineParser::tryParseLine)
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .collect(Collectors.toList());

        return indentedTreeParser.parseTrees(nodes, SbtNode::getLevel, this::nodeToDependency);
    }

    private Dependency nodeToDependency(SbtNode node) {
        ExternalId externalId = externalIdFactory.createMavenExternalId(node.getGroup(), node.getName(), node.getVersion());
        return new Dependency(node.getName(), node.getVersion(), externalId);
    }

}
