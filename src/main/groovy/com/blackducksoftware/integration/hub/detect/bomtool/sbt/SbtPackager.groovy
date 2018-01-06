/*
 * Copyright (C) 2018 Black Duck Software, Inc.
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

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtDependencyModule
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.reports.parse.SbtReportParser
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

import groovy.transform.TypeChecked

@TypeChecked
class SbtPackager {
    private final Logger logger = LoggerFactory.getLogger(SbtPackager.class)

    ExternalIdFactory externalIdFactory;

    SbtPackager(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    List<SbtDependencyModule> makeModuleAggregate(List<File> reportFiles, String include, String exclude) {
        def parser = new SbtReportParser()
        def resolver = new SbtDependencyResolver(externalIdFactory)
        def filter = new ExcludedIncludedFilter(exclude, include)
        def aggregator = new SbtModuleAggregator()

        List<SbtDependencyModule> modules = reportFiles.collect { reportFile->
            logger.debug("Parsing SBT report file : ${reportFile.getCanonicalPath()}")
            def xml = new XmlSlurper().parse(reportFile)
            def report = parser.parseReportFromXml(xml)
            def tree = resolver.resolveReport(report)
            tree
        }

        def includedModules = modules.findAll { module ->
            filter.shouldInclude(module.configuration)
        }

        if (modules.size() <= 0) {
            logger.warn("No sbt configurations were found in report folder.")
            return null
        } else if (includedModules.size() <= 0) {
            logger.warn("Although ${modules.size()} configs were found, none were included.")
            return null
        }

        aggregator.aggregateModules(includedModules)
    }
}