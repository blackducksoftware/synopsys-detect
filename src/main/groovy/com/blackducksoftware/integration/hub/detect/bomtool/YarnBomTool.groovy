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
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnPackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

@Component
@groovy.transform.CompileStatic
class YarnBomTool extends BomTool {
    @Autowired
    YarnPackager yarnPackager

    @Override
    public BomToolType getBomToolType() {
        BomToolType.YARN
    }

    @Override
    public boolean isBomToolApplicable() {
        detectFileManager.containsAllFiles(sourcePath, 'yarn.lock')
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        final String yarnLockText = detectFileManager.findFile(sourceDirectory, 'yarn.lock').getText(StandardCharsets.UTF_8.toString())
        final Set<DependencyNode> dependencyNodes = yarnPackager.parse(yarnLockText)
        final ExternalId externalId = new PathExternalId(Forge.NPM, sourcePath)
        final def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, externalId, dependencyNodes)

        return [detectCodeLocation]
    }
}
