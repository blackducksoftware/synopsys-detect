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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;

public class SbtModuleAggregator {
    private final Logger logger = LoggerFactory.getLogger(SbtModuleAggregator.class);

    List<SbtDependencyModule> aggregateModules(final List<SbtDependencyModule> modules) {
        final List<SbtAggregate> aggregates = uniqueAggregates(modules);

        return aggregates.stream().map(aggregate -> {
            final SbtDependencyModule aggregated = new SbtDependencyModule();
            aggregated.name = aggregate.name;
            aggregated.version = aggregate.version;
            aggregated.org = aggregate.org;

            final MutableDependencyGraph graph = new MutableMapDependencyGraph();
            aggregated.graph = graph;

            final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            modules.forEach(module -> {
                if (moduleEqualsAggregate(module, aggregate)) {
                    combiner.addGraphAsChildrenToRoot(graph, module.graph);
                }
            });

            return aggregated;
        }).collect(Collectors.toList());
    }

    boolean moduleEqualsAggregate(final SbtDependencyModule module, final SbtAggregate aggregate) {
        final boolean namesMatch = module.name == aggregate.name;
        final boolean versionsMatch = module.version == aggregate.version;
        final boolean groupsMatch = module.org == aggregate.org;

        return namesMatch && groupsMatch && versionsMatch;
    }

    SbtAggregate moduleToAggregate(final SbtDependencyModule module) {
        final SbtAggregate aggregate = new SbtAggregate(module.name, module.org, module.version);
        return aggregate;
    }

    List<SbtAggregate> uniqueAggregates(final List<SbtDependencyModule> modules) {
        final List<SbtAggregate> found = new ArrayList<>();
        modules.forEach(module -> {
            final SbtAggregate aggregate = moduleToAggregate(module);
            if (!found.contains(aggregate)) {
                found.add(aggregate);
            }
        });
        return found;
    }
}
