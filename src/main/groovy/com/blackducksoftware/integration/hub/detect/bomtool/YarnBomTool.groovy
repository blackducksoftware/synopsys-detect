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

    private List<String> yarnLockText
    private String yarnExePath

    YarnBomTool() {}

    YarnBomTool(List<String> yarnLockText) {
        this.yarnLockText = yarnLockText
    }

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
        def detectCodeLocation

        File yarnLockFile = detectFileManager.findFile(sourceDirectory, 'yarn.lock')
        yarnLockText = Files.readAllLines(yarnLockFile.toPath(), StandardCharsets.UTF_8)
        yarnExePath = findExecutablePath(ExecutableType.YARN, true, detectConfiguration.getYarnPath())

        if (detectConfiguration.yarnProductionDependenciesOnly) {
            dependencyGraph = extractGraphFromYarnListCommand(yarnExePath)
        } else {
            dependencyGraph = yarnPackager.parseYarnLock(yarnLockText)
        }

        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, sourcePath)
        detectCodeLocation = new DetectCodeLocation.Builder(getBomToolType(), sourcePath, externalId, dependencyGraph).build()

        return [detectCodeLocation]
    }

    DependencyGraph extractGraphFromYarnListCommand(String yarnExePath) {
        File yarnListOutputFile = detectFileManager.createFile(BomToolType.YARN, OUTPUT_FILE)
        File yarnListErrorFile = detectFileManager.createFile(BomToolType.YARN, ERROR_FILE)

        def exeArgs = ['list', '-prod']

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
        ExternalId extId = new ExternalId(Forge.NPM)
        UUID rndUUID = UUID.randomUUID()
        String rootName = "detectRootNode - ${rndUUID}"
        extId.name = rootName
        Dependency root = new Dependency(rootName, extId)

        Map<String, String> componentVersions = getYarnLockAsMap(yarnLockText)

        int depth
        Dependency currentDep, parentDep
        for (String line : yarnListAsList) {

            if (line.toLowerCase().startsWith("yarn list")
                    || line.toLowerCase().startsWith("done in")
                    || line.toLowerCase().startsWith("warning")) {
                continue
            }

            line = line.replaceAll("├─", " ").replaceAll("│", " ").replaceAll("└─", " ")
            depth = getDepth(line)

            if (depth == 0) {
                currentDep = getDependencyFromLine(line, componentVersions)
                graph.addChildToRoot(currentDep)
                parentDep = currentDep
            }

            if (depth >= 1) {
                currentDep = getDependencyFromLine(line, componentVersions)
                logger.debug(currentDep.name + "@" + currentDep.version + " is being added as a child of " + parentDep.name + "@" + parentDep.version)
                graph.addChildWithParent(currentDep, parentDep)
            }
        }

        graph
    }

    static HashMap<String, String> getYarnLockAsMap(List<String> yarnLockText) {
        Map<String, String> result = new HashMap<>()
        String thisDependency = ""
        String thisVersion

        for (String line : yarnLockText) {
            if (!line.trim()) {
                continue
            }

            if (line.trim().startsWith('#')) {
                continue
            }

            int level = YarnPackager.getLineLevel(line)
            if (level == 0) {
                thisDependency = line.trim().replace("\"", "").replaceAll(":", "")
                continue
            }

            if (level == 1 && line.trim().startsWith('version')) {
                thisVersion = line.trim().split(' ')[1].replaceAll('"', '')
                result.put(thisDependency, thisVersion)
                result.put(thisDependency.split("@")[0] + "@" + thisVersion, thisVersion)
            }
        }

        result
    }

    private Dependency getDependencyFromLine(String line, Map<String, String> compVersions) {
        String fuzzyName = grabFuzzyName(line)
        String name = fuzzyName.split("@")[0]
        String version

        if (compVersions.containsKey(fuzzyName)) {
            version = compVersions.get(fuzzyName)
        } else {
            version = fuzzyName.split("@")[1]
            logger.debug("Couldn't find the resolved version of " + fuzzyName + "in yarn lock. Using " + version + ".")
        }

        logger.debug("Found version " + version + " for " + fuzzyName)

        ExternalId extId = new ExternalId(Forge.NPM)
        extId.name = name
        extId.version = version

        new Dependency(name, version, extId)
    }

    static int getDepth(String s) {
        // how many spaces (S) does it start with? then depth, in this case is, D = (S - 2)/3
        Pattern pattern = Pattern.compile(" ")
        Matcher matcher = pattern.matcher(s)
        int count = matcher.getCount()

        Math.floorDiv(count - 2, 3)
    }

    static String grabFuzzyName(String line) {
        // TODO TEST THIS!!
        // e.g.
        // ├─ whatwg-url@4.8.0 >> whatwg-url@4.8.0
        // OR
        // │  ├─ tr46@~0.0.3 >> tr46@~0.0.3

        // [a-zA-Z\d-]+@.+[\dx]$
        Pattern pattern = Pattern.compile("[ \\d.\\-a-zA-Z]+@.+")
        Matcher matcher = pattern.matcher(line)
        matcher.find()
        String result = matcher.group(0).trim()
        result
    }

}
