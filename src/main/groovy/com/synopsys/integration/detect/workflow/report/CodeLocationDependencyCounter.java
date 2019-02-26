/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;

public class CodeLocationDependencyCounter {
    public Map<CodeLocationType, Integer> aggregateCountsByGroup(final Map<CodeLocation, Integer> codeLocations) {
        final Map<CodeLocationType, Integer> dependencyCounts = new HashMap<>();
        for (final Entry<CodeLocation, Integer> countEntry : codeLocations.entrySet()) {
            final CodeLocationType group = countEntry.getKey().getCodeLocationType();
            if (!dependencyCounts.containsKey(group)) {
                dependencyCounts.put(group, 0);
            }
            dependencyCounts.put(group, dependencyCounts.get(group) + countEntry.getValue());
        }
        return dependencyCounts;
    }

    public Map<CodeLocation, Integer> countCodeLocations(final List<CodeLocation> codeLocations) {
        final Map<CodeLocation, Integer> dependencyCounts = new HashMap<>();
        for (final CodeLocation codeLocation : codeLocations) {
            if (!dependencyCounts.containsKey(codeLocation)) {
                dependencyCounts.put(codeLocation, 0);
            }
            dependencyCounts.put(codeLocation, dependencyCounts.get(codeLocation) + countCodeLocationDependencies(codeLocation));
        }
        return dependencyCounts;
    }

    private int countCodeLocationDependencies(final CodeLocation codeLocation) {
        return countDependencies(new ArrayList<ExternalId>(), codeLocation.getDependencyGraph().getRootDependencyExternalIds(), codeLocation.getDependencyGraph());
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
