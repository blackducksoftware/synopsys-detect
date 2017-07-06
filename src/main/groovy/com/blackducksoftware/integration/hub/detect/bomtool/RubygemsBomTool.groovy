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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.RubygemsNodePackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class RubygemsBomTool extends BomTool {
    @Autowired
    RubygemsNodePackager rubygemsNodePackager

    BomToolType getBomToolType() {
        return BomToolType.RUBYGEMS
    }

    boolean isBomToolApplicable() {
        detectFileManager.containsAllFiles(sourcePath, 'Gemfile.lock')
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        File sourceDirectory = detectConfiguration.sourceDirectory

        def gemlockFile = new File(sourceDirectory, 'Gemfile.lock')
        String gemlock = gemlockFile.getText(StandardCharsets.UTF_8.name())

        List<DependencyNode> dependencies = rubygemsNodePackager.extractProjectDependencies(gemlock)
        Set<DependencyNode> dependenciesSet = new HashSet<>(dependencies)
        ExternalId externalId = new PathExternalId(Forge.RUBYGEMS, sourcePath)

        def codeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, "", "", externalId, dependenciesSet)
        [codeLocation]
    }
}