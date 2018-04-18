/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.bomtool.go.godep.GoGodepsBomTool
import com.blackducksoftware.integration.hub.detect.bomtool.go.vndr.GoVndrBomTool
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class GoDepBomTool extends BomTool<GoDepApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(GoDepBomTool.class)

    public static final String GOPKG_LOCK_FILENAME= 'Gopkg.lock'
    public static final String GOFILE_FILENAME_PATTERN= '*.go'

    @Autowired
    GoGodepsBomTool goGodepsBomTool

    @Autowired
    GoVndrBomTool goVndrBomTool

    @Autowired
    DepPackager goPackager

    @Autowired
    ExternalIdFactory externalIdFactory

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_DEP
    }

    //TODO: BOM-FINDER Fix GO to not apply if other GOs apply.
    @Override
    public GoDepApplicableResult isBomToolApplicable(File directory) {
        def goPkg = detectFileManager.findFile(directory, GOPKG_LOCK_FILENAME);
        if (goPkg) {
            def goFiles = detectFileManager.findFilesToDepth(directory, GOFILE_FILENAME_PATTERN, detectConfiguration.getSearchDepth())
            if (goFiles) {
                def goExe = executableManager.getExecutablePath(ExecutableType.GO, true, directory.toString())
                if (goExe) {
                    return new GoDepApplicableResult(directory, goPkg, goFiles, goExe);
                }
            }
        }

        return null;
    }

    @Override
    BomToolExtractionResult extractDetectCodeLocations(GoDepApplicableResult applicable) {
        String goDepExecutable = findGoDepExecutable(applicable.directoryString)

        DependencyGraph graph = goPackager.makeDependencyGraph(applicable.directoryString, goDepExecutable)
        if(graph == null) {
            graph = new MutableMapDependencyGraph()
        }
        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, applicable.directoryString)
        DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), applicable.directoryString, externalId, graph).build()

        bomToolExtractionResultsFactory.fromCodeLocations([detectCodeLocation], getBomToolType(), applicable.directory)
    }

    private String findGoDepExecutable(String directory) {
        String goDepPath = detectConfiguration.goDepPath
        if (StringUtils.isBlank(goDepPath)) {
            def goDep = getBuiltGoDep()
            if (goDep.exists()) {
                goDepPath = goDep.getAbsolutePath()
            } else {
                goDepPath = executableManager.getExecutablePath(ExecutableType.GO_DEP, true, directory)
            }
        }
        if (!goDepPath?.trim()) {
            def goExecutable = executableManager.getExecutablePath(ExecutableType.GO, true, directory)
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
