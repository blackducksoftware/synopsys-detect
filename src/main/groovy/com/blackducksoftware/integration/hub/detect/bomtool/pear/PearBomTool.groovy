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
package com.blackducksoftware.integration.hub.detect.bomtool.pear

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked
import groovy.util.slurpersupport.GPathResult

@Component
class PearBomTool extends BomTool<PearApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(PearBomTool.class)

    static final String PACKAGE_XML_FILENAME = 'package.xml'

    @Autowired
    PearDependencyFinder pearDependencyFinder

    @Override
    public BomToolType getBomToolType() {
        BomToolType.PEAR
    }

    @Override
    @TypeChecked
    public PearApplicableResult isBomToolApplicable(File directory) {
        File packageXml = detectFileManager.findFile(directory, PACKAGE_XML_FILENAME)

        if (packageXml) {
            def pearExe = executableManager.getExecutablePathOrOverride(ExecutableType.PEAR, true, directory, detectConfiguration.getPearPath())
            if (pearExe) {
                return new PearApplicableResult(directory, packageXml, pearExe);
            } else {
                logger.warn("Could not find a ${executableManager.getExecutableName(ExecutableType.PEAR)} executable")
            }
        }

        return null;
    }

    @Override
    public BomToolExtractionResult extractDetectCodeLocations(PearApplicableResult applicable) {
        ExecutableOutput pearListing = executableRunner.runExe(applicable.pearExe, 'list')
        ExecutableOutput pearDependencies = executableRunner.runExe(applicable.pearExe, 'package-dependencies', PACKAGE_XML_FILENAME)

        File packageFile = detectFileManager.findFile(applicable.directory, PACKAGE_XML_FILENAME)
        GPathResult packageXml = new XmlSlurper().parseText(packageFile.text)
        String rootName = packageXml.name
        String rootVersion = packageXml.version.release

        DependencyGraph dependencyGraph = pearDependencyFinder.parsePearDependencyList(pearListing, pearDependencies)
        def detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), applicable.directory, externalIdFactory.createNameVersionExternalId(Forge.PEAR, rootName, rootVersion),
                dependencyGraph).bomToolProjectName(rootName).bomToolProjectVersionName(rootVersion).build()

        bomToolExtractionResultsFactory.fromCodeLocations([detectCodeLocation], getBomToolType(), applicable.directory)
    }
}
