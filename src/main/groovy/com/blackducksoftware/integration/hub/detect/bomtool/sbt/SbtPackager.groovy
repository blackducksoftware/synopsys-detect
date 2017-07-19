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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

import groovy.util.slurpersupport.GPathResult

@Component
public class SbtPackager {

    private final Logger logger = LoggerFactory.getLogger(SbtPackager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    public DependencyNode makeDependencyNode(List<GPathResult> xmls, String include, String exclude){
        def parser = new SbtReportParser();
        def resolver = new SbtDependencyResolver();
        def filter = new ExcludedIncludedFilter(include, exclude);
        def aggregator = new SbtConfigurationAggregator();

        List<SbtConfigurationDependencyTree> configurations;

        configurations = xmlReports.collect { xmlReport ->
            def report = parser.parseReportFromXml(xml)
            def tree = resolver.resolveReportDependencies(report)
            tree
        }.findAll{tree ->
            filter.shouldInclude(tree.configuration)
        }

        if (configurations.size() <= 0){
            logger.warn("No sbt configurations were included.");
        }

        aggregator.aggregateConfigurations(configurations);
    }
}