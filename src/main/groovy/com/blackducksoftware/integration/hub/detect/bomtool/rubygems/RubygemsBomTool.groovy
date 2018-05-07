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
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class RubygemsBomTool extends BomTool<RubygemsApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(RubygemsBomTool.class)

    public static final String GEMFILE_LOCK_FILENAME= 'Gemfile.lock'

    @Autowired
    RubygemsNodePackager rubygemsNodePackager

    @Autowired
    ExternalIdFactory externalIdFactory

    BomToolType getBomToolType() {
        return BomToolType.RUBYGEMS
    }

    RubygemsApplicableResult isBomToolApplicable(File directory) {
        File gemlock = detectFileFinder.findFile(directory, GEMFILE_LOCK_FILENAME)

        if (gemlock) {
            return new RubygemsApplicableResult(directory, gemlock);
        }

        return null;
    }

    BomToolExtractionResult extractDetectCodeLocations(RubygemsApplicableResult applicable) {
        File sourceDirectory = detectConfiguration.sourceDirectory

        def gemlockFile = new File(sourceDirectory, GEMFILE_LOCK_FILENAME)
        List<String> gemlockText = Files.readAllLines(gemlockFile.toPath(), StandardCharsets.UTF_8)

        DependencyGraph dependencyGraph = rubygemsNodePackager.extractProjectDependencies(gemlockText)
        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.RUBYGEMS, applicable.directoryString)

        def codeLocation = new DetectCodeLocation.Builder(getBomToolType(), applicable.directoryString, externalId, dependencyGraph).build()

        bomToolExtractionResultsFactory.fromCodeLocations([codeLocation], getBomToolType(), applicable.directory)
    }
}
