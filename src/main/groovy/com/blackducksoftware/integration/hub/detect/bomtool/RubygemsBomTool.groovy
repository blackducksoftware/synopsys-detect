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
package com.blackducksoftware.integration.hub.detect.bomtool

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
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.RubygemsNodePackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class RubygemsBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(RubygemsBomTool.class)

    public static final String GEMFILE_LOCK_FILENAME= 'Gemfile.lock'

    @Autowired
    RubygemsNodePackager rubygemsNodePackager

    @Autowired
    ExternalIdFactory externalIdFactory

    BomToolType getBomToolType() {
        return BomToolType.RUBYGEMS
    }

    boolean isBomToolApplicable() {
        detectFileManager.containsAllFiles(sourcePath, GEMFILE_LOCK_FILENAME)
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        File sourceDirectory = detectConfiguration.sourceDirectory

        def gemlockFile = new File(sourceDirectory, GEMFILE_LOCK_FILENAME)
        List<String> gemlockText = Files.readAllLines(gemlockFile.toPath(), StandardCharsets.UTF_8)

        DependencyGraph dependencyGraph = rubygemsNodePackager.extractProjectDependencies(gemlockText)
        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.RUBYGEMS, sourcePath)

        def codeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, externalId, dependencyGraph)
        [codeLocation]
    }
}