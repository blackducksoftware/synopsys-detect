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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtPackager
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtDependencyModule
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtProject
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class SbtBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(SbtBomTool.class)

    static final String BUILD_SBT_FILENAME = 'build.sbt'
    static final String REPORT_FILE_DIRECTORY = "${File.separator}target${File.separator}resolution-cache${File.separator}reports"
    static final String REPORT_SEARCH_PATTERN = 'resolution-cache'
    static final String REPORT_DIRECTORY = "reports"
    static final String REPORT_FILE_PATTERN = '*.xml'
    static final String PROJECT_FOLDER = 'project'

    SbtPackager sbtPackager = new SbtPackager()

    @Autowired
    HubSignatureScanner hubSignatureScanner

    BomToolType getBomToolType() {
        return BomToolType.SBT
    }

    boolean isBomToolApplicable() {
        String buildDotSbt = detectFileManager.findFile(sourcePath, BUILD_SBT_FILENAME)

        if (buildDotSbt) {
            return true
        } else {
            return false
        }
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        String included = detectConfiguration.getSbtIncludedConfigurationNames()
        String excluded = detectConfiguration.getSbtExcludedConfigurationNames()

        int depth = detectConfiguration.getSearchDepth()

        SbtProject project = extractProject(depth, included, excluded)

        List<DetectCodeLocation> codeLocations = new ArrayList<DetectCodeLocation>()

        project.modules.each { module ->
            DetectCodeLocation codeLocation = new DetectCodeLocation(getBomToolType(), module.name, project.projectName, project.projectVersion, project.projectExternalId, module.graph)
            codeLocations.add(codeLocation)
        }

        if (!codeLocations) {
            logger.error("Unable to find any dependency information.")
            return []
        } else {
            return codeLocations
        }
    }

    SbtProject extractProject(int depth, String included, String excluded) {
        def rawModules = extractModules(depth, included, excluded)
        def modules = rawModules.findAll{ it.graph != null }
        def skipped = rawModules.size() - modules.size()
        if (skipped > 0) {
            logger.error("Skipped ${skipped}")
        }
        def result = new SbtProject()
        result.bomToolType = getBomToolType()
        result.modules = modules

        if (modules.size() == 1) {
            result.projectName = modules[0].name
            result.projectVersion = modules[0].version
            result.projectExternalId = new MavenExternalId(modules[0].org, modules[0].name, modules[0].version)
        } else {
            logger.warn("Found more than one root project, using source path for project name.")
            result.projectName = detectFileManager.extractFinalPieceFromPath(sourcePath)
            result.projectVersion = findFirstModuleVersion(modules, result.projectName, "root")
            result.projectExternalId = new PathExternalId(Forge.MAVEN, sourcePath)

            if (result.projectVersion == null && modules.size() > 1) {
                logger.warn("Getting version from first project: " + modules[0].name)
                result.projectVersion = modules[0].version
            } else {
                logger.info("Using the source path or root task for version.")
            }
        }

        result
    }

    String findFirstModuleVersion(List<SbtDependencyModule> modules, String... names) {
        String version = null
        modules.each{
            if (version == null && it.name != null && names.contains(it.name)) {
                logger.debug("Matched ${it.name} to project version.")
                version = it.version
            }
        }
        return version
    }

    List<SbtDependencyModule> extractModules(int depth, String included, String excluded) {
        List<File> sbtFiles = detectFileManager.findFilesToDepth(sourcePath, BUILD_SBT_FILENAME, depth) as List
        List<File> resolutionCaches = detectFileManager.findDirectoriesContainingDirectoriesToDepth(sourcePath, REPORT_SEARCH_PATTERN, depth) as List

        List<SbtDependencyModule> modules = new ArrayList<SbtDependencyModule>()
        List<String> usedReports = new ArrayList<String>()

        sbtFiles.each { sbtFile ->
            logger.debug("Found SBT build file : ${sbtFile.getCanonicalPath()}")
            File sbtDirectory = sbtFile.getParentFile()
            File reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY)

            def foundModules = extractReportModules(reportPath, sbtDirectory, included, excluded, usedReports)
            modules.addAll(foundModules)
        }

        resolutionCaches.each { resCache ->
            logger.debug("Found resolution cache : ${resCache.getCanonicalPath()}")
            File reportPath = new File(resCache, REPORT_DIRECTORY)
            def foundModules = extractReportModules(reportPath, resCache.getParentFile(), included, excluded, usedReports)
            modules.addAll(foundModules)
        }

        List<File> additionalTargets = detectFileManager.findFilesToDepth(sourcePath, 'target', depth) as List
        List<File> scanned = new ArrayList<File>()
        if (additionalTargets) {
            additionalTargets.each { file ->
                if (!isInProject(file, sourcePath) && isNotChildOfScanned(file, scanned)) {
                    hubSignatureScanner.registerPathToScan(file)
                    scanned.add(file)
                }
            }
        }

        modules
    }

    Boolean isNotChildOfScanned(File folder, List<File> scanned) {
        for (def scan : scanned) {
            if (folder.getCanonicalPath().startsWith(scan.getCanonicalPath())) {
                return false
            }
        }
        return true
    }

    Boolean isInProject(File file, String sourcePath) {
        def projectPath = new File(sourcePath, PROJECT_FOLDER)
        return file.getCanonicalPath().startsWith(projectPath.getCanonicalPath())
    }

    List<SbtDependencyModule> extractReportModules(File reportPath, File source, String included, String excluded, List<String> usedReports) {
        List<SbtDependencyModule> modules = new ArrayList<SbtDependencyModule>()
        String canonical = reportPath.getCanonicalPath()
        if (usedReports.contains(canonical)) {
            logger.debug("Skipping already processed report folder: " + canonical)
        } else if (isInProject(reportPath, sourcePath)) {
            logger.debug("Skipping reports in project folder: ${reportPath.getCanonicalPath()}")
        } else {
            usedReports.add(canonical)
            List<File> reportFiles = detectFileManager.findFiles(reportPath, REPORT_FILE_PATTERN) as List
            if (reportFiles == null || reportFiles.size() <= 0) {
                logger.debug("No reports were found in: ${reportPath}")
            } else {
                List<SbtDependencyModule> aggregatedModules = sbtPackager.makeModuleAggregate(reportFiles, included, excluded)

                if (aggregatedModules == null) {
                    logger.debug("No dependencies were generated for report folder: ${reportPath}")
                } else {
                    logger.debug("Found ${aggregatedModules.size()} aggregate dependencies in report folder: ${reportPath}")
                    aggregatedModules.each{ aggregatedModule ->
                        logger.debug("Generated root node of ${aggregatedModule.name} ${aggregatedModule.version} ")

                        aggregatedModule.sourcePath = source.getCanonicalPath()

                        modules.add(aggregatedModule)
                    }
                }
            }
        }
        modules
    }
}