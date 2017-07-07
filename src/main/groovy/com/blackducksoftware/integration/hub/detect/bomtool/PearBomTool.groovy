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
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearDependencyFinder
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class PearBomTool extends BomTool {
    private String pearExePath

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
        DependencyNode rootDependencyNode = pearDependencyFinder.parsePearDependencyList(sourcePath, pearExePath)
        def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, rootDependencyNode)

        [detectCodeLocation]
    }

    private String findPearExePath() {
        if (detectConfiguration.getPearPath()) {
            return detectConfiguration.getPearPath()
        }

        executableManager.getPathOfExecutable(ExecutableType.PEAR)
    }
}
