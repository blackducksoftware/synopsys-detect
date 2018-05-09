/*
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear

import org.apache.commons.lang3.BooleanUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

import groovy.transform.TypeChecked
import groovy.util.slurpersupport.GPathResult

@Component

class PearDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(PearDependencyFinder.class)

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExternalIdFactory externalIdFactory


    public PearParseResult parse(File packageFile, ExecutableOutput pearListing, ExecutableOutput pearDependencies) {
        PearParseResult result = new PearParseResult();
        GPathResult packageXml = new XmlSlurper().parseText(packageFile.text);
        result.name = packageXml.name;
        String version = packageXml.version.release;
        result.version = version;
        result.dependencyGraph = parsePearDependencyList(pearListing, pearDependencies);
        return result;
    }

    @TypeChecked
    public DependencyGraph parsePearDependencyList(ExecutableOutput pearListing, ExecutableOutput pearDependencies) {
        DependencyGraph graph = new MutableMapDependencyGraph()

        if (pearDependencies.errorOutput || pearListing.errorOutput) {
            logger.error("There was an error during execution.")
        } else if (!pearDependencies.standardOutput || !pearListing.standardOutput) {
            logger.error("No information retrieved from running pear commands")
        } else {
            def nameList = findDependencyNames(pearDependencies.standardOutputAsList)
            graph = createPearDependencyGraphFromList(pearListing.standardOutputAsList, nameList)
        }

        graph
    }

    @TypeChecked
    private List<String> findDependencyNames(List<String> content) {
        def nameList = []

        if (content.size() > 5) {
            def listing = content[5..-1]
            listing.each { line ->
                String[] dependencyInfo = line.trim().split(' ')
                dependencyInfo -= ''

                String dependencyName = dependencyInfo[2].trim()
                String dependencyRequired = dependencyInfo[0].trim()

                if (dependencyName) {
                    if (!detectConfiguration.getPearOnlyRequiredDependencies()) {
                        nameList.add(dependencyName.split('/')[-1])
                    } else {
                        if (BooleanUtils.toBoolean(dependencyRequired)) {
                            nameList.add(dependencyName.split('/')[-1])
                        }
                    }
                }
            }
        }

        nameList
    }

    @TypeChecked
    private DependencyGraph createPearDependencyGraphFromList(List<String> dependencyList, List<String> dependencyNames) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph()

        if (dependencyList.size() > 3) {
            def listing = dependencyList[3..-1]
            listing.each { line ->
                String[] dependencyInfo = line.trim().split(' ')
                dependencyInfo -= ''

                String packageName = dependencyInfo[0].trim()
                String packageVersion = dependencyInfo[1].trim()

                if (dependencyInfo && dependencyNames.contains(packageName)) {
                    def child = new Dependency(packageName, packageVersion, externalIdFactory.createNameVersionExternalId(Forge.PEAR, packageName, packageVersion))

                    graph.addChildToRoot(child)
                }
            }
        }

        graph
    }
}
