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
package com.blackducksoftware.integration.hub.packman.bomtool.go

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.bomtool.GoBomTool
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner
import com.google.gson.Gson

@Component
class GoPackager {
    private final Logger logger = LoggerFactory.getLogger(GoPackager.class)

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    Gson gson

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    FileFinder fileFinder

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, String goExecutable) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(BomToolType.GO, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(GoBomTool.GOLANG, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)
        def goDirectories = fileFinder.findDirectoriesContainingFiles(new File(sourcePath), '*.go', NumberUtils.toInt(packmanProperties.searchDepth));
        GoDepParser goDepParser = new GoDepParser(gson, projectInfoGatherer)
        def children = new ArrayList<DependencyNode>()
        goDirectories.each {
            String goDepContents = getGoDepContents(it, goExecutable)
            DependencyNode child = goDepParser.parseGoDep(goDepContents)
            children.add(child)
        }
        if (packmanProperties.goAggregate) {
            root.children = children
            return [root]
        } else {
            return children
        }
    }

    private String getGoDepContents(File goDirectory, String goExecutable) {
        logger.info("Running ${goExecutable} save on path ${goDirectory.getAbsolutePath()}")
        Executable executable = new Executable(goDirectory, goExecutable, ['save'])
        executableRunner.executeLoudly(executable)
        def goDepsDirectory = new File(goDirectory, "Godeps")
        def goDepsFile = new File(goDepsDirectory, "Godeps.json")
        def goDepContents = goDepsFile.text
        FileUtils.deleteDirectory(goDepsDirectory)
        goDepContents
    }
}
