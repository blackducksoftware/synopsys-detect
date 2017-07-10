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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class CpanPackager {
    private final Logger logger = LoggerFactory.getLogger(CpanPackager.class)

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    CpanListParser cpanListParser

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> makeDependencyNodes(File sourceDirectory, String cpanListText, String directDependenciesText) {
        Map<String, NameVersionNode> allModules = cpanListParser.parse(cpanListText)
        List<String> directModuleNames = getDirectModuleNames(directDependenciesText)

        Set<DependencyNode> dependencyNodes = []
        directModuleNames.each { moduleName ->
            def nameVersionNode = allModules[moduleName]
            if (nameVersionNode) {
                DependencyNode module = nameVersionNodeTransformer.createDependencyNode(Forge.CPAN, nameVersionNode)
                dependencyNodes += module
            } else {
                logger.warn("Could node find resolved version for module: ${moduleName}")
            }
        }

        dependencyNodes
    }

    private List<String> getDirectModuleNames(String directDependenciesText) {
        List<String> modules = []
        for (String line : directDependenciesText.split('\n')) {
            if (!line?.trim()) {
                continue
            }
            if (line.contains('-->') || (line.contains(' ... ') && line.contains('Configuring'))) {
                continue
            }
            modules += line.split('~')[0].trim()
        }

        modules
    }
}
