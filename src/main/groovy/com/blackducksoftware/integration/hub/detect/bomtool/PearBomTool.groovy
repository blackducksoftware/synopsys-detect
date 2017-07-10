/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
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

        NameVersionNodeImpl nameVersionModel = pearDependencyFinder.findNameVersion(sourcePath)

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
