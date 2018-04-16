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
package com.blackducksoftware.integration.hub.detect.bomtool

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnPackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
@TypeChecked
class YarnBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(YarnBomTool.class)
    public static final String OUTPUT_FILE = 'detect_yarn_proj_dependencies.txt'
    public static final String ERROR_FILE = 'detect_yarn_error.txt'

    private String yarnExePath

    @Autowired
    YarnPackager yarnPackager

    @Autowired
    ExternalIdFactory externalIdFactory

    @Override
    BomToolType getBomToolType() {
        BomToolType.YARN
    }

    @Override
    boolean isBomToolApplicable() {
        detectFileManager.containsAllFiles(sourcePath, 'yarn.lock')
        yarnExePath = findExecutablePath(ExecutableType.YARN, true, detectConfiguration.getYarnPath())
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        DependencyGraph dependencyGraph
        ExternalId externalId
        def detectCodeLocation
        yarnExePath = findExecutablePath(ExecutableType.YARN, true, detectConfiguration.getYarnPath())
        if (detectConfiguration.yarnProductionDependenciesOnly) {
            dependencyGraph = extractGraphFromYarnListCommand(yarnExePath)
            externalId = externalIdFactory.createPathExternalId(Forge.NPM, sourcePath)
            detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), sourcePath, externalId, dependencyGraph).build()
        } else {
            File yarnLockFile = detectFileManager.findFile(sourceDirectory, 'yarn.lock')
            List<String> yarnText = Files.readAllLines(yarnLockFile.toPath(), StandardCharsets.UTF_8)
            dependencyGraph = yarnPackager.parseYarnLock(yarnText)
            externalId = externalIdFactory.createPathExternalId(Forge.NPM, sourcePath)
            detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), sourcePath, externalId, dependencyGraph).build()
        }

        return [detectCodeLocation]
    }

    DependencyGraph extractGraphFromYarnListCommand(String yarnExePath) {
        File yarnListOutputFile = detectFileManager.createFile(BomToolType.YARN, OUTPUT_FILE)
        File yarnListErrorFile = detectFileManager.createFile(BomToolType.YARN, ERROR_FILE)

        def exeArgs = ['list', '--prod']

        Executable yarnListExe = new Executable(new File(sourcePath), yarnExePath, exeArgs)
        executableRunner.executeToFile(yarnListExe, yarnListOutputFile, yarnListErrorFile)

        if (!(yarnListOutputFile.length() > 0)) {
            if (yarnListErrorFile.length() > 0) {
                logger.error("Error when running yarn list --prod command")
                logger.debug(yarnListErrorFile.text)
            } else {
                logger.warn("Nothing returned from yarn list --prod command")
            }
        }

        extractGraphFromYarnListFile(yarnListOutputFile.readLines())

    }

    DependencyGraph extractGraphFromYarnListFile(List yarnListAsList) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph()
        ExternalId extId

        UUID rndUUID = UUID.randomUUID()
        Dependency root = new Dependency("detectRootNode - ${rndUUID}", extId)
        graph.addChildToRoot(root)

        int depth
        Dependency currentDep, parentDep, grandParentDep
        String fuzzyName, name, version
        for (String line : yarnListAsList) {
            line = line.replaceAll("├─", " ").replaceAll("│", " ").replaceAll("└─", " ")
            depth = getDepth(line)

            if (depth == 0) {
                currentDep = getDependencyFromLine(line)
                graph.addChildToRoot(currentDep)
                parentDep = currentDep
            }

            if (depth >= 1) {
                currentDep = getDependencyFromLine(line)
                logger.debug(currentDep.name + " is being added as a child of " + parentDep.name)
                graph.addChildWithParent(currentDep, parentDep)
            }
        }

        graph
    }

    private Dependency getDependencyFromLine(String line) {
        String fuzzyName = grabFuzzyName(line)
        String name = fuzzyName.split("@")[0]
        String version = fuzzyName.split("@")[1]

        ExternalId extId = new ExternalId(Forge.NPM)
        extId.name = name
        extId.version = version

        new Dependency(name, version, extId)
    }

    int getDepth(String s) {
        // how many spaces (S) does it start with? then depth, in this case is, D = (S - 2)/3
        Pattern pattern = Pattern.compile(" ")
        Matcher matcher = pattern.matcher(s)
        int count = matcher.getCount()

        int depth = Math.floorDiv(count - 2, 3)

        logger.debug("Current parsing depth : " + depth)
        depth
    }

    String grabFuzzyName(String line) {
        // TODO TEST THIS!!
        // e.g.
        // ├─ whatwg-url@4.8.0 >> whatwg-url@4.8.0
        // OR
        // │  ├─ tr46@~0.0.3 >> tr46@~0.0.3

        // [a-zA-Z\d-]+@.+[\dx]$
//        Pattern pattern = Pattern.compile("(a-zA-Z)+.+") //"(a-zA-Z)+@.+(\\dx)\$")
//        Matcher matcher = pattern.matcher(line)
//        logger.info(matcher.matches().toString())
//        logger.info(matcher.groupCount().toString())
        String result
        result = line.split(" ")[-1]
        logger.debug("Dependency found: '" + result + "'")

        result
    }

}
