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
package com.blackducksoftware.integration.hub.packman.packagemanager.go

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner

class GoPackager {
    private final Logger logger = LoggerFactory.getLogger(GoPackager.class)

    @Autowired
    ExecutableRunner executableRunner

    private final ProjectInfoGatherer projectInfoGatherer

    public GoPackager(final ProjectInfoGatherer projectInfoGatherer) {
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, String goExecutable) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(PackageManagerType.GO, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(Forge.GOGET, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)
        GoDepParser goDepParser = new GoDepParser()
        def goDirectories = findGoDirectories(new File(sourcePath))

        def children = new ArrayList<DependencyNode>()
        goDirectories.each {
            String goDepContents = getGoDepContents(it)
            DependencyNode child = goDepParser.parseGoDep(goDepContents)
            children.add(child)
        }
        root.children = children
        //TODO aggregate all the results automatically or not??
        [root]
    }

    private File[] findGoDirectories(File file) {
        def directories = new ArrayList<File>()
        if (file.isDirectory()) {
            boolean containsGoFiles = false;
            file.listFiles().each {
                if (it.isDirectory()) {
                    findGoDirectories(it)
                } else if (it.getName().contains('.go')) {
                    containsGoFiles = true
                }
            }
            if (containsGoFiles) {
                directories.add(file)
            }
        }
        directories
    }

    private String getGoDepContents(File goDirectory, String goExecutable) {
        logger.info("Running ${goExecutable} save on path ${goDirectory.getAbsolutePath()}")
        Executable executable = new Executable(goDirectory, goExecutable, ['save'])
        executableRunner.executeLoudly(executable)
        def goDepsFile = new File(goDirectory, "Godeps")
        goDepsFile = new File(goDepsFile, "Godeps.json")
        // get Godeps/Godeps.json contents
        goDepsFile.text
    }
}
