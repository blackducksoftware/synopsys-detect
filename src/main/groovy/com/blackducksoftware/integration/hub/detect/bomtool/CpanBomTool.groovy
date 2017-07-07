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
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanPackager
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class CpanBomTool extends BomTool {
    @Autowired
    CpanPackager cpanPackager

    private String cpanExecutablePath
    private String cpanmExecutablePath

    @Override
    public BomToolType getBomToolType() {
        BomToolType.CPAN
    }

    @Override
    public boolean isBomToolApplicable() {
        def containsFiles = detectFileManager.containsAllFiles(sourcePath, 'cpanfile')
        cpanExecutablePath = executableManager.getPathOfExecutable(ExecutableType.CPAN, detectConfiguration.getCpanPath())
        cpanmExecutablePath = executableManager.getPathOfExecutable(ExecutableType.CPANM, detectConfiguration.getCpanmPath())

        containsFiles && cpanExecutablePath && cpanmExecutablePath
    }

    @Override
    public List<DetectCodeLocation> extractDetectCodeLocations() {
        Set<DependencyNode> dependenciesSet = cpanPackager.makeDependencyNodes(detectConfiguration.sourceDirectory, cpanExecutablePath, cpanmExecutablePath)
        ExternalId externalId = new PathExternalId(Forge.CPAN, detectConfiguration.sourcePath)
        def detectCodeLocation = new DetectCodeLocation(BomToolType.CPAN, detectConfiguration.sourcePath, "", "", externalId, dependenciesSet)

        [detectCodeLocation]
    }
}
