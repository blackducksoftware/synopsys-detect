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
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtPackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

@Component
class SbtBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(SbtBomTool.class)

    static final String BUILD_SBT_FILENAME = 'build.sbt'
    static final String REPORT_FILE_DIRECTORY = "${File.separator}target${File.separator}resolution-cache${File.separator}reports"
    static final String REPORT_SEARCH_PATTERN = 'resolution-cache'
    static final String REPORT_DIRECTORY = "reports"
    static final String REPORT_FILE_PATTERN = '*.xml'

    SbtPackager sbtPackager = new SbtPackager()

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
        List<File> sbtFiles = detectFileManager.findFilesToDepth(sourcePath, BUILD_SBT_FILENAME, depth)
        List<File> resolutionCaches = detectFileManager.findDirectoriesContainingDirectoriesToDepth(sourcePath, REPORT_SEARCH_PATTERN, depth)

        List<DetectCodeLocation> codeLocations = new ArrayList<DetectCodeLocation>()
        List<String> usedReports = new ArrayList<String>();

        sbtFiles.each { sbtFile ->
            logger.debug("Found SBT build file : ${sbtFile.getCanonicalPath()}")
            def sbtDirectory = sbtFile.getParentFile()
            def reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY)

            List<File> reportFiles = detectFileManager.findFiles(reportPath, REPORT_FILE_PATTERN)
            usedReports.add(reportPath.getCanonicalPath());

            if (reportFiles == null || reportFiles.size() <= 0){
                logger.warn("Found a build.sbt ${sbtFile.getCanonicalPath()}, but no reports: ${reportPath}")
            }else{
                DependencyNode node = sbtPackager.makeDependencyNode(reportFiles, included, excluded)

                if (node == null) {
                    logger.info("No dependencies were generated for report folder: ${reportPath}")
                } else {
                    def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sbtDirectory.getCanonicalPath(), node)
                    codeLocations.add(detectCodeLocation)
                }
            }
        }

        resolutionCaches.each { resCache ->
            File reportPath = new File(resCache, REPORT_DIRECTORY);
            String canonical = reportPath.getCanonicalPath();
            if (usedReports.contains(canonical)){
                logger.debug("Skipping already processed report folder: " + canonical);
            }else{
                usedReports.add(canonical);
                List<File> reportFiles = detectFileManager.findFiles(reportPath, REPORT_FILE_PATTERN)
                if (reportFiles == null || reportFiles.size() <= 0){
                    logger.warn("No reports were found in resolution-cache: ${resCache}")
                }else{
                    DependencyNode node = sbtPackager.makeDependencyNode(reportFiles, included, excluded)

                    if (node == null) {
                        logger.warn("No dependencies were generated for report folder: ${reportPath}")
                    } else {
                        logger.debug("Found report folder: ${reportPath}")
                        def detectCodeLocation = new DetectCodeLocation(getBomToolType(), resCache.getParentFile().getCanonicalPath(), node)
                        codeLocations.add(detectCodeLocation)
                    }
                }
            }
        }

        if (!codeLocations) {
            logger.error("Unable to find any dependency information.")
            return []
        } else {
            return codeLocations
        }
    }
}