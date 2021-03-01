/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class CodeLocationDependencyCounter {
    public Map<String, Integer> aggregateCountsByCreatorName(final Map<DetectCodeLocation, Integer> codeLocations) {
        final Map<String, Integer> dependencyCounts = new HashMap<>();
        for (final Map.Entry<DetectCodeLocation, Integer> countEntry : codeLocations.entrySet()) {
            final Optional<String> group = countEntry.getKey().getCreatorName();

            if (group.isPresent()) {
                if (!dependencyCounts.containsKey(group.get())) {
                    dependencyCounts.put(group.get(), 0);
                }
                dependencyCounts.put(group.get(), dependencyCounts.get(group.get()) + countEntry.getValue());
            }
        }
        return dependencyCounts;
    }

    public Map<DetectCodeLocation, Integer> countCodeLocations(final Set<DetectCodeLocation> codeLocations) {
        final Map<DetectCodeLocation, Integer> dependencyCounts = new HashMap<>();
        for (final DetectCodeLocation codeLocation : codeLocations) {
            if (!dependencyCounts.containsKey(codeLocation)) {
                dependencyCounts.put(codeLocation, 0);
            }
            dependencyCounts.put(codeLocation, dependencyCounts.get(codeLocation) + countCodeLocationDependencies(codeLocation));
        }
        return dependencyCounts;
    }

    private int countCodeLocationDependencies(final DetectCodeLocation codeLocation) {
        return countDependencies(new ArrayList<>(), codeLocation.getDependencyGraph().getRootDependencyExternalIds(), codeLocation.getDependencyGraph());
    }

    private int countDependencies(final List<ExternalId> processed, final Set<ExternalId> remaining, final DependencyGraph graph) {
        int sum = 0;
        for (final ExternalId dependency : remaining) {
            if (processed.contains(dependency)) {
                continue;
            }
            processed.add(dependency);
            sum += 1 + countDependencies(processed, graph.getChildrenExternalIdsForParent(dependency), graph);
        }
        return sum;
    }
}
