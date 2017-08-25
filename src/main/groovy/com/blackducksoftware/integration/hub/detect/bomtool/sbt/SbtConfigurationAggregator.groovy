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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtConfigurationDependencyTree

public class SbtConfigurationAggregator {
    private final Logger logger = LoggerFactory.getLogger(SbtConfigurationAggregator.class)

    List<DependencyNode> aggregateConfigurations(List<SbtConfigurationDependencyTree> configurations) {
        def aggregates = uniqueAggregates(configurations)

        def nodes = aggregates.collect{ aggregate ->
            DependencyNode root = new DependencyNode(new MavenExternalId(aggregate.org, aggregate.name, aggregate.version))
            root.name = aggregate.name
            root.version = aggregate.version
            root.children = new ArrayList<DependencyNode>()
            configurations.each {config ->
                if (configurationEqualsAggregate(config, aggregate)){
                    root.children.addAll(config.rootNode.children)
                }
            }
            root
        }

        return nodes
    }

    boolean configurationEqualsAggregate(SbtConfigurationDependencyTree config, SbtAggregate aggregate) {

        def namesMatch = config.rootNode.name == aggregate.name
        def versionsMatch = config.rootNode.version == aggregate.version

        def id = config.rootNode.externalId as MavenExternalId
        def groupsMatch = id.group == aggregate.org

        return namesMatch && groupsMatch && versionsMatch
    }

    SbtAggregate configurationToAggregate(SbtConfigurationDependencyTree config) {
        def id = config.rootNode.externalId as MavenExternalId
        def aggregate = new SbtAggregate(config.rootNode.name, id.group, config.rootNode.version)
        return aggregate
    }

    List<SbtAggregate> uniqueAggregates(List<SbtConfigurationDependencyTree> configurations){
        List<SbtAggregate> found = new ArrayList<SbtAggregate>()
        configurations.each{config ->
            def aggregate = configurationToAggregate(config)
            if (!found.contains(aggregate)){
                found.add(aggregate)
            }
        }
        return found
    }
}
