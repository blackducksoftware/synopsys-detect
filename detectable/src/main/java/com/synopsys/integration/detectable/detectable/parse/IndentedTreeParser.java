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
package com.synopsys.integration.detectable.detectable.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class IndentedTreeParser<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<DependencyGraph> parseTrees(List<T> nodes, Function<T, Integer> getLevel, Function<T, Dependency> getDependency) {
        History<Dependency> history = new History<>();
        List<DependencyGraph> graphs = new ArrayList<>();
        MutableMapDependencyGraph current = null;
        for (T node : nodes) {
            int level = getLevel.apply(node);
            try {
                history.clearDeeperThan(level);
            } catch (final IllegalStateException e) {
                logger.warn(String.format("Problem parsing node '%s': %s", node.toString(), e.getMessage()));
            }

            Dependency dependency = getDependency.apply(node);
            if (!history.isEmpty() && current != null) {
                current.addParentWithChild(history.getLast(), dependency);
            } else {
                current = new MutableMapDependencyGraph();
                graphs.add(current);
                current.addChildToRoot(dependency);
            }

            history.add(dependency);
        }
        return graphs;
    }
}
