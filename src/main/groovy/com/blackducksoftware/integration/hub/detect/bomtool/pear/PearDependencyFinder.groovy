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
package com.blackducksoftware.integration.hub.detect.bomtool.pear

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class PearDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(PearDependencyFinder.class)
    private final Forge pear = new Forge('pear', '/')

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    ExecutableRunner executableRunner

    public DependencyNode parsePearDependencyList(String rootDirectoryPath, String exePath) {
        def pearListExe = new Executable(new File(rootDirectoryPath), exePath, ['list'])

        ExecutableOutput pearDependencyList = executableRunner.execute(pearListExe)

        if(pearDependencyList.errorOutput) {
            logger.error("There was an error during execution.\n${pearDependencyList.standardOutput}")
        } else {
            DependencyNode resultNode = createRootNode(rootDirectoryPath)
            createPearDependencyNodeFromList(pearDependencyList.standardOutput, resultNode)
            return resultNode
        }

        []
    }

    private void createPearDependencyNodeFromList(String list, DependencyNode parentNode) {
        String dependencyList = list.split('VERSION STATE')[1]
        String[] lines = dependencyList.trim().split('\n')

        lines.each {
            /*
             * This split turns the line into an array of
             * 0 Dependency name
             * 1 Dependency version
             * 2 Dependency state
             */
            String[] dependencyInfo = it.split(' ')
            dependencyInfo -= ''

            String nodeName = dependencyInfo[0].trim()
            String nodeVersion = dependencyInfo[1].trim()

            if(dependencyInfo) {
                def newNode = new DependencyNode(nodeName, nodeVersion, new NameVersionExternalId(pear, nodeName, nodeVersion))

                parentNode.children.add(newNode)
            }
        }
    }

    private DependencyNode createRootNode(String sourcePath) {
        File packageFile = detectFileManager.findFile(sourcePath, 'package.xml')

        def packageXml = new XmlSlurper().parseText(packageFile.text)
        String rootName = packageXml.name
        String rootVersion = packageXml.version.api

        def rootNode = new DependencyNode(rootName, rootVersion, new NameVersionExternalId(pear, rootName, rootVersion))
        rootNode
    }
}
