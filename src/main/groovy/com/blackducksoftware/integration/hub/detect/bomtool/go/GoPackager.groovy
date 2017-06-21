/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.go

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.GoBomTool
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException
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
    DetectConfiguration detectConfiguration

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, String goExecutable) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(BomToolType.GO, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(GoBomTool.GOLANG, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)
        def goDirectories = findDirectoriesContainingGoFilesToDepth(new File(sourcePath), detectConfiguration.getSearchDepth());
        GoDepParser goDepParser = new GoDepParser(gson, projectInfoGatherer)
        def children = new ArrayList<DependencyNode>()
        goDirectories.each {
            String goDepContents = getGoDepContents(it, goExecutable)
            if (goDepContents?.trim()){
                DependencyNode child = goDepParser.parseGoDep(goDepContents)
                children.add(child)
            }
        }
        if (detectConfiguration.getGoAggregate()) {
            root.children = children
            return [root]
        } else {
            return children
        }
    }

    private String getGoDepContents(File goDirectory, String goExecutable) {
        def vendorDirectory = new File(goDirectory, "vendor")
        def goDepsDirectory = new File(goDirectory, "Godeps")
        def goDepsFile = new File(goDepsDirectory, "Godeps.json")
        if (goDepsFile.exists()) {
            return goDepsFile.text
        }
        boolean previousVendorFile = vendorDirectory.exists()
        def goDepContents = null
        try{
            logger.info("Running ${goExecutable} save on path ${goDirectory.getAbsolutePath()}")
            Executable executable = new Executable(goDirectory, goExecutable, ['save'])
            executableRunner.execute(executable)
        } catch (ExecutableRunnerException e){
            logger.error("Failed to run ${goExecutable} save on path ${goDirectory.getAbsolutePath()}, ${e.getMessage()}")
        }
        if (goDepsFile.exists()) {
            goDepContents = goDepsFile.text
            FileUtils.deleteDirectory(goDepsDirectory)
        }
        if (!previousVendorFile && vendorDirectory.exists()) {
            // cleanup the vendor directory if the save command created it
            FileUtils.deleteDirectory(vendorDirectory)
        }
        goDepContents
    }

    private File[] findDirectoriesContainingGoFilesToDepth(final File sourceDirectory, int maxDepth){
        return findDirectoriesContainingGoFilesRecursive(sourceDirectory, 0, maxDepth)
    }

    private File[] findDirectoriesContainingGoFilesRecursive(final File sourceDirectory, int currentDepth, int maxDepth){
        def files = new HashSet<File>()
        // we want to ignore the vendor and Godeps directory, they are the go cache https://blog.gopheracademy.com/advent-2015/vendor-folder/
        if (currentDepth > maxDepth || !sourceDirectory.isDirectory() || sourceDirectory.getName().equals('vendor') || sourceDirectory.getName().equals('Godeps')){
            return files
        }
        for (File file : sourceDirectory.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(findDirectoriesContainingGoFilesRecursive(file, currentDepth + 1, maxDepth))
            } else if (FilenameUtils.wildcardMatchOnSystem(file.getName(), '*.go')) {
                files.add(sourceDirectory)
            }
        }
        return new ArrayList<File>(files)
    }
}
