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

import org.springframework.beans.factory.annotation.Autowired

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.go.DepPackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class GoDepBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GoDepBomTool.class)

    public static final String GOPKG_LOCK_FILENAME= 'Gopkg.lock'

    public static final String GOFILE_FILENAME_PATTERN= '*.go'

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
        if (detectFileManager.containsAllFiles(sourcePath, GOPKG_LOCK_FILENAME)) {
            isTheBestGoBomTool = true
        } else  {
            boolean otherGoBomToolsWouldBeBetter = goGodepsBomTool.isBomToolApplicable() || goVndrBomTool.isBomToolApplicable()
            boolean foundGoFiles = detectFileManager.containsAllFilesToDepth(sourcePath, detectConfiguration.getSearchDepth(), GOFILE_FILENAME_PATTERN)
            if (foundGoFiles && otherGoBomToolsWouldBeBetter) {
                logger.debug("A different Go BomTool is applicable for source path $sourcePath")
            }
            if (!otherGoBomToolsWouldBeBetter && foundGoFiles) {
                isTheBestGoBomTool = true
            }
        }

        def goExecutablePath
        if (isTheBestGoBomTool) {
            goExecutablePath = executableManager.getExecutablePath(ExecutableType.GO, true, sourcePath)
        }
        if (isTheBestGoBomTool && !goExecutablePath) {
            logger.warn("Could not find the ${executableManager.getExecutableName(ExecutableType.GO)} executable")
        }

        goExecutablePath && isTheBestGoBomTool
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        String goDepExecutable = findGoDepExecutable()

        DependencyGraph graph = goPackager.makeDependencyGraph(sourcePath, goDepExecutable)
        ExternalId externalId = new PathExternalId(GOLANG, sourcePath)
        DetectCodeLocation detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, externalId, graph)

        [detectCodeLocation]
    }

    private String findGoDepExecutable() {
        String goDepPath = detectConfiguration.goDepPath
        if (StringUtils.isBlank(goDepPath)) {
            def goDep = getBuiltGoDep()
            if (goDep.exists()) {
                goDepPath = goDep.getAbsolutePath()
            } else {
                goDepPath = executableManager.getExecutablePath(ExecutableType.GO_DEP, true, sourcePath)
            }
        }
        if (!goDepPath?.trim()) {
            def goExecutable = executableManager.getExecutablePath(ExecutableType.GO, true, sourcePath)
            goDepPath = installGoDep(goExecutable)
        }
        goDepPath
    }

    private String installGoDep(String goExecutable) {
        File goDep = getBuiltGoDep()
        def goOutputDirectory = goDep.getParentFile()
        goOutputDirectory.mkdirs()
        logger.debug("Retrieving the Go Dep tool")
        Executable getGoDep = new Executable(goOutputDirectory, goExecutable, ['get', '-u', '-v', '-d', 'github.com/golang/dep/cmd/dep'])
        executableRunner.execute(getGoDep)

        logger.debug("Building the Go Dep tool in ${goOutputDirectory}")
        Executable buildGoDep = new Executable(goOutputDirectory, goExecutable, ['build', 'github.com/golang/dep/cmd/dep'])
        executableRunner.execute(buildGoDep)
        goDep.getAbsolutePath()
    }

    private File getBuiltGoDep() {
        def goOutputDirectory = new File(detectConfiguration.outputDirectory, 'Go')
        new File(goOutputDirectory, executableManager.getExecutableName(ExecutableType.GO_DEP))
    }
}