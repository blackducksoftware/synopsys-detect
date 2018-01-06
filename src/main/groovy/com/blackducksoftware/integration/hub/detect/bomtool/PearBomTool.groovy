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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearDependencyFinder
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked
import groovy.util.slurpersupport.GPathResult

@Component
class PearBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(PearBomTool.class)

    static final String PACKAGE_XML_FILENAME = 'package.xml'

    private String pearExePath

    @Autowired
    PearDependencyFinder pearDependencyFinder

    @Autowired
    ExternalIdFactory externalIdFactory

    @Override
    public BomToolType getBomToolType() {
        BomToolType.PEAR
    }

    @Override
    @TypeChecked
    public boolean isBomToolApplicable() {
        boolean containsPackageXml = detectFileManager.containsAllFiles(sourcePath, PACKAGE_XML_FILENAME)

        if (containsPackageXml) {
            pearExePath = findExecutablePath(ExecutableType.PEAR, true, detectConfiguration.getPearPath())
            if (!pearExePath) {
                logger.warn("Could not find a ${executableManager.getExecutableName(ExecutableType.PEAR)} executable")
            }
        }

        pearExePath && containsPackageXml
    }

    @Override
    public List<DetectCodeLocation> extractDetectCodeLocations() {
        ExecutableOutput pearListing = executableRunner.runExe(pearExePath, 'list')
        ExecutableOutput pearDependencies = executableRunner.runExe(pearExePath, 'package-dependencies', PACKAGE_XML_FILENAME)

        File packageFile = detectFileManager.findFile(sourcePath, PACKAGE_XML_FILENAME)
        GPathResult packageXml = new XmlSlurper().parseText(packageFile.text)
        String rootName = packageXml.name
        String rootVersion = packageXml.version.release

        DependencyGraph dependencyGraph = pearDependencyFinder.parsePearDependencyList(pearListing, pearDependencies)
        def detectCodeLocation = new DetectCodeLocation(
                getBomToolType(),
                sourcePath,
                rootName,
                rootVersion,
                externalIdFactory.createNameVersionExternalId(Forge.PEAR, rootName, rootVersion),
                dependencyGraph
                )

        [detectCodeLocation]
    }
}
