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
package com.synopsys.integration.detectable.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class GraphSerializer {
    private static final Logger logger = LoggerFactory.getLogger(GraphSerializer.class);

    public static String serialize(final DependencyGraph graph) {

        final StringBuilder rootText = new StringBuilder();
        for (final Dependency dependency : graph.getRootDependencies()) {
            rootText.append("\t" + dependencyToString(dependency) + "\n");
        }

        final StringBuilder relationshipText = new StringBuilder();
        final Set<Dependency> processed = new HashSet<>();
        final Stack<Dependency> unprocessed = new Stack<>();
        unprocessed.addAll(graph.getRootDependencies());

        while (unprocessed.size() > 0) {
            final Dependency dependency = unprocessed.pop();
            if (processed.contains(dependency)) {
                continue;
            } else {
                processed.add(dependency);
            }
            final StringBuilder childText = new StringBuilder();
            int cnt = 0;
            for (final Dependency childDependency : graph.getChildrenForParent(dependency)) {
                cnt++;
                unprocessed.add(childDependency);
                childText.append("\t\t" + dependencyToString(childDependency) + "\n");
            }
            if (cnt > 0) {
                relationshipText.append("\t" + dependencyToString(dependency) + "\n");
                relationshipText.append(childText.toString());
            }
        }

        final StringBuilder graphText = new StringBuilder();
        graphText.append("Detect Graph v0.0.0" + "\n");
        graphText.append("\tRoot Dependencies (" + graph.getRootDependencies().size() + ")" + "\n");
        graphText.append("\tTotal Dependencies (" + graph.getRootDependencies().size() + ")" + "\n");
        graphText.append("Root Dependencies" + "\n");
        graphText.append(rootText.toString());
        graphText.append("Relationships" + "\n");
        graphText.append(relationshipText.toString());
        graphText.append("\n");

        logger.info(graphText.toString());
        return graphText.toString();
    }

    private static String dependencyToString(final Dependency dependency) {
        return dependency.getName() + "," + dependency.getVersion() + "," + externalIdToString(dependency.getExternalId());
    }

    private static String escape(final String target) {
        return target.replaceAll(",", "%comma%");
    }

    private static String externalIdToString(final ExternalId externalId) {
        final String forge = externalId.getForge().getName().toString();
        final String pieces = Arrays.stream(externalId.getExternalIdPieces()).map(GraphSerializer::escape).collect(Collectors.joining(","));
        return forge + "," + pieces;
    }
}
