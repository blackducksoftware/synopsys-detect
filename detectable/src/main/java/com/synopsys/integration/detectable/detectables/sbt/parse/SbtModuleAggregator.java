package com.synopsys.integration.detectable.detectables.sbt.parse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtAggregate;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtDependencyModule;

public class SbtModuleAggregator {
    private final Logger logger = LoggerFactory.getLogger(SbtModuleAggregator.class);

    public List<SbtDependencyModule> aggregateModules(List<SbtDependencyModule> modules) {
        Set<SbtAggregate> aggregates = uniqueAggregates(modules);
        logger.debug("Found unique aggregates: " + aggregates.size());

        return aggregates.stream().map(aggregate -> {
            SbtDependencyModule aggregated = new SbtDependencyModule();
            aggregated.setName(aggregate.getName());
            aggregated.setVersion(aggregate.getVersion());
            aggregated.setOrg(aggregate.getOrg());

            BasicDependencyGraph graph = new BasicDependencyGraph();
            aggregated.setGraph(graph);

            modules.forEach(module -> {
                if (moduleEqualsAggregate(module, aggregate)) {
                    logger.debug("Combining '" + module.getName() + "' with '" + aggregate.getName() + "'");
                    graph.copyGraphToRoot((BasicDependencyGraph) module.getGraph());
                }
            });

            return aggregated;
        }).collect(Collectors.toList());
    }

    private boolean moduleEqualsAggregate(SbtDependencyModule module, SbtAggregate aggregate) {
        boolean namesMatch = module.getName().equals(aggregate.getName());
        boolean versionsMatch = module.getVersion().equals(aggregate.getVersion());
        boolean groupsMatch = module.getOrg().equals(aggregate.getOrg());

        return namesMatch && groupsMatch && versionsMatch;
    }

    private SbtAggregate moduleToAggregate(SbtDependencyModule module) {
        SbtAggregate aggregate = new SbtAggregate(module.getName(), module.getOrg(), module.getVersion());
        return aggregate;
    }

    private Set<SbtAggregate> uniqueAggregates(List<SbtDependencyModule> modules) {
        return modules.stream().map(this::moduleToAggregate).collect(Collectors.toSet());
    }
}
