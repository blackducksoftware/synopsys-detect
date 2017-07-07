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
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
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

    public List<DependencyNode> makeDependencyNodes(File sourceDirectory, String cpanExecutablePath, String cpanmExecutablePath, String perlExecutablePath) {
        Map<String, NameVersionNode> allModules = getAllModulesMap(sourceDirectory, cpanExecutablePath)
        List<String> directModuleNames = getDirectModuleNames(sourceDirectory, cpanmExecutablePath)

        List<DependencyNode> dependencyNodes = []
        directModuleNames.each { moduleName ->
            def nameVersionNode = allModules[moduleName]
            if(nameVersionNode) {
                DependencyNode module = nameVersionNodeTransformer.createDependencyNode(Forge.CPAN, nameVersionNode)
                dependencyNodes += module
            } else {
                logger.warn("Could node find resolved version for module: ${moduleName}")
            }
        }

        dependencyNodes
    }

    private Map<String, NameVersionNode> getAllModulesMap(File sourceDirectory, String cpanExecutablePath) {
        def executable = new Executable(sourceDirectory, cpanExecutablePath, ['-l'])
        ExecutableOutput executableOutput = executableRunner.execute(executable)
        String listText = executableOutput.getStandardOutput()

        cpanListParser.parse(listText)
    }

    private List<String> getDirectModuleNames(File sourceDirectory, String cpanmExecutablePath) {
        def executable = new Executable(sourceDirectory, cpanmExecutablePath, ['--showdeps', '.'])
        ExecutableOutput executableOutput = executableRunner.execute(executable)
        String lines = executableOutput.getStandardOutput()

        List<String> modules = []
        for(String line : lines.split('\n')) {
            if(!line?.trim()) {
                continue
            }
            if(line.contains('-->') || (line.contains(' ... ') && line.contains('Configuring'))) {
                continue
            }
            modules += line.split('~')[0].trim()
        }

        modules
    }
}
