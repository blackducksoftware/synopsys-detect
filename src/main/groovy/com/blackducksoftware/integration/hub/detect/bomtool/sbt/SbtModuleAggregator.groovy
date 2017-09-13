/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.DependencyGraphCombiner
import com.blackducksoftware.integration.hub.bdio.simple.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.simple.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtAggregate
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtDependencyModule

import groovy.transform.TypeChecked

@TypeChecked
public class SbtModuleAggregator {
    private final Logger logger = LoggerFactory.getLogger(SbtModuleAggregator.class)

    List<SbtDependencyModule> aggregateModules(List<SbtDependencyModule> modules) {
        def aggregates = uniqueAggregates(modules)

        aggregates.collect{ aggregate ->
            SbtDependencyModule aggregated = new SbtDependencyModule()
            aggregated.name = aggregate.name
            aggregated.version = aggregate.name
            aggregated.org = aggregate.name

            MutableDependencyGraph graph = new MutableMapDependencyGraph()
            aggregated.graph = graph

            DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            modules.each {module ->
                if (moduleEqualsAggregate(module, aggregate)) {
                    combiner.addGraphAsChildrenToRoot(graph, module.graph)
                }
            }

            aggregated
        }
    }

    boolean moduleEqualsAggregate(SbtDependencyModule module, SbtAggregate aggregate) {
        def namesMatch = module.name == aggregate.name
        def versionsMatch = module.version == aggregate.version
        def groupsMatch = module.org == aggregate.org

        return namesMatch && groupsMatch && versionsMatch
    }

    SbtAggregate moduleurationToAggregate(SbtDependencyModule module) {
        def aggregate = new SbtAggregate(module.name, module.org, module.version)
        return aggregate
    }

    List<SbtAggregate> uniqueAggregates(List<SbtDependencyModule> modules) {
        List<SbtAggregate> found = new ArrayList<SbtAggregate>()
        modules.each{module ->
            def aggregate = moduleurationToAggregate(module)
            if (!found.contains(aggregate)) {
                found.add(aggregate)
            }
        }
        return found
    }
}
