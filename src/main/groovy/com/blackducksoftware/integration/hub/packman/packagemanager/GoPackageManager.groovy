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
package com.blackducksoftware.integration.hub.packman.packagemanager

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.packagemanager.go.GoPackager
import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableManager
@Component
class GoPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(GradlePackageManager.class)

    @Autowired
    FileFinder fileFinder

    @Autowired
    GoPackager goPackager

    @Autowired
    ExecutableManager executableManager

    @Autowired
    PackmanProperties packmanProperties

    PackageManagerType getPackageManagerType() {
        PackageManagerType.GO
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        //TODO should be recursive or not?
        fileFinder.findFile(sourcePath, '*.go')
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def godepExecutable = findGoExecutable()
        goPackager.makeDependencyNodes(sourcePath, godepExecutable)
    }

    private String findGoExecutable() {
        String godepPath = packmanProperties.godepPath
        if (StringUtils.isBlank(godepPath)) {
            logger.info('packman.godep.path not set in config - trying to find gradle on the PATH')
            godepPath = executableManager.getPathOfExecutable(ExecutableType.GO)
        }

        godepPath
    }
}