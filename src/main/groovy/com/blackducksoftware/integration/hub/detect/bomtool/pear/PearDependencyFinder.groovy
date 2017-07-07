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
        def pearPackageDependencyExe = new Executable(new File(rootDirectoryPath), exePath, [
            'package-dependencies',
            'package.xml'
        ])

        ExecutableOutput pearPackageDependencyNames = executableRunner.execute(pearPackageDependencyExe)
        ExecutableOutput pearDependencyList = executableRunner.execute(pearListExe)

        if (pearDependencyList.errorOutput || pearPackageDependencyNames.errorOutput) {
            logger.error("There was an error during execution.")
        } else if (!pearDependencyList.standardOutput || !pearPackageDependencyNames.standardOutput) {
            logger.error("No information retrieved from running pear commands")
        } else {
            def nameList = findDependencyNames(pearPackageDependencyNames.standardOutput)
            DependencyNode resultNode = createRootNode(rootDirectoryPath)
            createPearDependencyNodeFromList(pearDependencyList.standardOutput, nameList, resultNode)
            return resultNode
        }

        []
    }

    private List<String> findDependencyNames(String list) {
        def nameList = []
        String[] content = list.split('\n')

        def listing = content[5..-1]
        listing.each { line ->
            /*
             * These next lines turn the line into an array of
             * 0 Dependency required
             * 1 Dependency type
             * 2 Dependency name
             * 3+ extra unnecessary info
             */
            String[] dependencyInfo = line.trim().split(' ')
            dependencyInfo -= ''

            String nodeName = dependencyInfo[2].trim()

            if (nodeName) {
                nameList.add(nodeName.split('/')[-1])
            }
        }

        nameList
    }

    private DependencyNode createRootNode(String sourcePath) {
        File packageFile = detectFileManager.findFile(sourcePath, 'package.xml')

        def packageXml = new XmlSlurper().parseText(packageFile.text)
        String rootName = packageXml.name
        String rootVersion = packageXml.version.api

        def rootNode = new DependencyNode(rootName, rootVersion, new NameVersionExternalId(pear, rootName, rootVersion))
        rootNode
    }

    private void createPearDependencyNodeFromList(String list, List<String> dependencyNames, DependencyNode parentNode) {
        String[] dependencyList = list.split('\n')
        def listing = dependencyList[3..-1]

        listing.each { line ->
            /*
             * These next lines turn the line into an array of
             * 0 Dependency name
             * 1 Dependency version
             * 2 Dependency state
             */
            String[] dependencyInfo = line.split(' ')
            dependencyInfo -= ''

            String nodeName = dependencyInfo[0].trim()
            String nodeVersion = dependencyInfo[1].trim()

            if (dependencyInfo && dependencyNames.contains(nodeName)) {
                def newNode = new DependencyNode(nodeName, nodeVersion, new NameVersionExternalId(pear, nodeName, nodeVersion))

                parentNode.children.add(newNode)
            }
        }
    }
}
