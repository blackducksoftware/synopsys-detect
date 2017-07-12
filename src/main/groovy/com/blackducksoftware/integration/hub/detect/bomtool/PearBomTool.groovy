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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearDependencyFinder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

@Component
class PearBomTool extends BomTool {
    private String pearExePath
    final static Forge PEAR = new Forge('pear', '/')

    @Autowired
    PearDependencyFinder pearDependencyFinder

    @Override
    public BomToolType getBomToolType() {
        BomToolType.PEAR
    }

    @Override
    public boolean isBomToolApplicable() {
        boolean containsPackageXml = detectFileManager.containsAllFiles(sourcePath, 'package.xml')
        pearExePath = findPearExePath()

        pearExePath && containsPackageXml
    }

    @Override
    public List<DetectCodeLocation> extractDetectCodeLocations() {
        ExecutableOutput pearListing = runExe('list')
        ExecutableOutput pearDependencies = runExe('package-dependencies', 'package.xml')

        File packageFile = detectFileManager.findFile(sourcePath, 'package.xml')
        NameVersionNodeImpl nameVersionModel = pearDependencyFinder.findNameVersion(packageFile)

        Set<DependencyNode> childDependencyNodes = pearDependencyFinder.parsePearDependencyList(pearListing, pearDependencies)
        def detectCodeLocation = new DetectCodeLocation(
                getBomToolType(),
                sourcePath,
                nameVersionModel.name,
                nameVersionModel.version,
                new NameVersionExternalId(PEAR, nameVersionModel.name, nameVersionModel.version),
                childDependencyNodes
                )

        [detectCodeLocation]
    }

    private String findPearExePath() {
        if (detectConfiguration.getPearPath()) {
            return detectConfiguration.getPearPath()
        }

        executableManager.getPathOfExecutable(ExecutableType.PEAR)
    }

    private ExecutableOutput runExe(String... commands) {
        def pearExe = new Executable(new File(sourcePath), pearExePath, commands.toList())
        executableRunner.execute(pearExe)
    }
}
