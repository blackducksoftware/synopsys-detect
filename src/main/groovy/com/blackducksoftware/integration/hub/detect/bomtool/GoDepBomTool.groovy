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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.go.DepPackager
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

@Component
class GoDepBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GoDepBomTool.class)

    public static final Forge GOLANG = new Forge("golang",":")

    @Autowired
    GoGodepsBomTool goGodepsBomTool

    @Autowired
    GoVndrBomTool goVndrBomTool

    @Autowired
    DepPackager goPackager

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_DEP
    }

    @Override
    public boolean isBomToolApplicable() {
        boolean isTheBestGoBomTool = false
        if (detectFileManager.containsAllFiles(sourcePath, 'Gopkg.lock')) {
            isTheBestGoBomTool = true
        } else  {
            boolean otherGoBomToolsWouldBeBetter = goGodepsBomTool.isBomToolApplicable() || goVndrBomTool.isBomToolApplicable()
            if (!otherGoBomToolsWouldBeBetter && detectFileManager.containsAllFilesToDepth(sourcePath, detectConfiguration.getSearchDepth(), '*.go')) {
                isTheBestGoBomTool = true
            }
        }

        def goExecutablePath
        if (isTheBestGoBomTool) {
            goExecutablePath = executableManager.getPathOfExecutable(ExecutableType.GO)
        }

        goExecutablePath && isTheBestGoBomTool
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        String goDepExecutable = findGoDepExecutable()

        List<DependencyNode> dependencies = goPackager.makeDependencyNodes(sourcePath, goDepExecutable)
        ExternalId externalId = new PathExternalId(GOLANG, sourcePath)
        DetectCodeLocation detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, "", "", externalId, new HashSet(dependencies))

        [detectCodeLocation]
    }

    private String findGoDepExecutable() {
        String goDepPath = detectConfiguration.goDepPath
        if (StringUtils.isBlank(goDepPath)) {
            def goDep = getBuiltGoDep()
            if (goDep.exists()) {
                goDepPath = goDep.getAbsolutePath()
            } else {
                goDepPath = executableManager.getPathOfExecutable(ExecutableType.GO_DEP)
            }
        }
        if (!goDepPath?.trim()) {
            def goExecutable = executableManager.getPathOfExecutable(ExecutableType.GO)
            goDepPath = installGoDep(goExecutable)
        }
        goDepPath
    }

    private String installGoDep(String goExecutable) {
        File goDep = getBuiltGoDep()
        def goOutputDirectory = goDep.getParentFile()
        goOutputDirectory.mkdirs()
        logger.debug("Retrieving the Go Dep tool")
        Executable getGoDep = new Executable(goOutputDirectory, goExecutable, [
            'get',
            '-u',
            '-v',
            '-d',
            'github.com/golang/dep/cmd/dep'
        ])
        executableRunner.execute(getGoDep)

        logger.debug("Building the Go Dep tool in ${goOutputDirectory}")
        Executable buildGoDep = new Executable(goOutputDirectory, goExecutable, [
            'build',
            'github.com/golang/dep/cmd/dep'
        ])
        executableRunner.execute(buildGoDep)
        goDep.getAbsolutePath()
    }

    private File getBuiltGoDep() {
        def goOutputDirectory = new File(detectConfiguration.outputDirectory, 'Go')
        new File(goOutputDirectory, executableManager.getExecutableName(ExecutableType.GO_DEP))
    }
}