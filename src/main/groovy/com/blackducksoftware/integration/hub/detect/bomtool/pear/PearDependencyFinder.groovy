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
package com.blackducksoftware.integration.hub.detect.bomtool.pear

import org.apache.commons.lang3.BooleanUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.PearBomTool
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class PearDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(PearDependencyFinder.class)

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectConfiguration detectConfiguration

    public Set<DependencyNode> parsePearDependencyList(ExecutableOutput pearListing, ExecutableOutput pearDependencies) {
        Set<DependencyNode> childNodes = []

        if (pearDependencies.errorOutput || pearListing.errorOutput) {
            logger.error("There was an error during execution.")
        } else if (!pearDependencies.standardOutput || !pearListing.standardOutput) {
            logger.error("No information retrieved from running pear commands")
        } else {
            def nameList = findDependencyNames(pearDependencies.standardOutput)
            childNodes = createPearDependencyNodeFromList(pearListing.standardOutput, nameList)
        }

        childNodes
    }

    private List<String> findDependencyNames(String list) {
        def nameList = []
        String[] content = list.split('\n')

        def listing = content[5..-1]
        listing.each { line ->
            String[] dependencyInfo = line.trim().split(' ')
            dependencyInfo -= ''

            String dependencyName = dependencyInfo[2].trim()
            String dependencyRequired = dependencyInfo[0].trim()

            if (dependencyName) {
                if (detectConfiguration.getPearNotRequiredDependencies()) {
                    nameList.add(dependencyName.split('/')[-1])
                } else {
                    if (BooleanUtils.toBoolean(dependencyRequired)) {
                        nameList.add(dependencyName.split('/')[-1])
                    }
                }
            }
        }

        nameList
    }

    private Set<DependencyNode> createPearDependencyNodeFromList(String list, List<String> dependencyNames) {
        Set<DependencyNode> childrenNodes = []
        String[] dependencyList = list.split('\n')

        def listing = dependencyList[3..-1]
        listing.each { line ->
            String[] dependencyInfo = line.trim().split(' ')
            dependencyInfo -= ''

            String packageName = dependencyInfo[0].trim()
            String packageVersion = dependencyInfo[1].trim()

            if (dependencyInfo && dependencyNames.contains(packageName)) {
                def newNode = new DependencyNode(packageName, packageVersion, new NameVersionExternalId(PearBomTool.PEAR, packageName, packageVersion))

                childrenNodes.add(newNode)
            }
        }

        childrenNodes
    }
}
