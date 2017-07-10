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

import org.apache.commons.lang3.BooleanUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.PearBomTool
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class PearDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(PearDependencyFinder.class)

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectConfiguration detectConfiguration

    public Set<DependencyNode> parsePearDependencyList(ExecutableOutput pearListing, ExecutableOutput pearDependencies) {
        Set<DependencyNode> childNodes = []

        if (pearDependencies.errorOutput || pearListing.errorOutput) {
            logger.error("There was an error during execution.")
        } else if (!pearDependencies.standardOutput || !pearListing.standardOutput) {
            logger.error("No information retrieved from running pear commands")
        } else {
            def nameList = findDependencyNames(pearDependencies.standardOutput)
            childNodes = createPearDependencyNodeFromList(pearListing.standardOutput, nameList)
        }

        childNodes
    }

    public NameVersionNodeImpl findNameVersion(String sourcePath) {
        File packageFile = detectFileManager.findFile(sourcePath, 'package.xml')

        def packageXml = new XmlSlurper().parseText(packageFile.text)
        String rootName = packageXml.name
        String rootVersion = packageXml.version.api

        def nameVersionModel = new NameVersionNodeImpl()
        nameVersionModel.name = rootName
        nameVersionModel.version = rootVersion
        nameVersionModel
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
            String nodeRequired = dependencyInfo[0].trim()

            if (nodeName) {
                if (detectConfiguration.getPearNotRequiredDependencies()) {
                    nameList.add(nodeName.split('/')[-1])
                } else {
                    if (BooleanUtils.toBoolean(nodeRequired)) {
                        nameList.add(nodeName.split('/')[-1])
                    }
                }
            }
        }

        nameList
    }

    private Set<DependencyNode> createPearDependencyNodeFromList(String list, List<String> dependencyNames) {
        Set<DependencyNode> childrenNodes = []

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
                def newNode = new DependencyNode(nodeName, nodeVersion, new NameVersionExternalId(PearBomTool.PEAR, nodeName, nodeVersion))

                childrenNodes.add(newNode)
            }
        }

        childrenNodes
    }
}
