/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtAggregate;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtDependencyModule;

public class SbtModuleAggregator {
    private final Logger logger = LoggerFactory.getLogger(SbtModuleAggregator.class);

    public List<SbtDependencyModule> aggregateModules(final List<SbtDependencyModule> modules) {
        final Set<SbtAggregate> aggregates = uniqueAggregates(modules);
        logger.debug("Found unique aggregates: " + aggregates.size());

        return aggregates.stream().map(aggregate -> {
            final SbtDependencyModule aggregated = new SbtDependencyModule();
            aggregated.setName(aggregate.getName());
            aggregated.setVersion(aggregate.getVersion());
            aggregated.setOrg(aggregate.getOrg());

            final MutableDependencyGraph graph = new MutableMapDependencyGraph();
            aggregated.setGraph(graph);

            final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            modules.forEach(module -> {
                if (moduleEqualsAggregate(module, aggregate)) {
                    logger.debug("Combining '" + module.getName() + "' with '" + aggregate.getName() + "'");
                    combiner.addGraphAsChildrenToRoot(graph, module.getGraph());
                }
            });

            return aggregated;
        }).collect(Collectors.toList());
    }

    private boolean moduleEqualsAggregate(final SbtDependencyModule module, final SbtAggregate aggregate) {
        final boolean namesMatch = module.getName().equals(aggregate.getName());
        final boolean versionsMatch = module.getVersion().equals(aggregate.getVersion());
        final boolean groupsMatch = module.getOrg().equals(aggregate.getOrg());

        return namesMatch && groupsMatch && versionsMatch;
    }

    private SbtAggregate moduleToAggregate(final SbtDependencyModule module) {
        final SbtAggregate aggregate = new SbtAggregate(module.getName(), module.getOrg(), module.getVersion());
        return aggregate;
    }

    private Set<SbtAggregate> uniqueAggregates(final List<SbtDependencyModule> modules) {
        return modules.stream().map(this::moduleToAggregate).collect(Collectors.toSet());
    }
}
