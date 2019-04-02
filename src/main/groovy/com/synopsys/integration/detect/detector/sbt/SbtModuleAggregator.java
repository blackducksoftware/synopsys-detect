/**
 * synopsys-detect
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
package com.synopsys.integration.detect.detector.sbt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;

public class SbtModuleAggregator {
    private final Logger logger = LoggerFactory.getLogger(SbtModuleAggregator.class);

    public List<SbtDependencyModule> aggregateModules(final List<SbtDependencyModule> modules) {
        final Set<SbtAggregate> aggregates = uniqueAggregates(modules);
        logger.debug("Found unique aggregates: " + aggregates.size());

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
                    logger.debug("Combining '" + module.name + "' with '" + aggregate.name + "'");
                    combiner.addGraphAsChildrenToRoot(graph, module.graph);
                }
            });

            return aggregated;
        }).collect(Collectors.toList());
    }

    private boolean moduleEqualsAggregate(final SbtDependencyModule module, final SbtAggregate aggregate) {
        final boolean namesMatch = module.name.equals(aggregate.name);
        final boolean versionsMatch = module.version.equals(aggregate.version);
        final boolean groupsMatch = module.org.equals(aggregate.org);

        return namesMatch && groupsMatch && versionsMatch;
    }

    private SbtAggregate moduleToAggregate(final SbtDependencyModule module) {
        final SbtAggregate aggregate = new SbtAggregate(module.name, module.org, module.version);
        return aggregate;
    }

    private Set<SbtAggregate> uniqueAggregates(final List<SbtDependencyModule> modules) {
        return modules.stream().map(module -> moduleToAggregate(module)).collect(Collectors.toSet());
    }
}
