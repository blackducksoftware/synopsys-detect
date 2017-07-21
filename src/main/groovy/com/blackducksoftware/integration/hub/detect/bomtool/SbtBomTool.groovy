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
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class SbtBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(SbtBomTool.class)

    static final String BUILD_SBT_FILENAME = 'build.sbt'
    static final String REPORT_FILE_DIRECTORY = "${File.separator}target${File.separator}resolution-cache${File.separator}reports"
    static final String REPORT_FILE_PATTERN = '*.xml'

    SbtPackager sbtPackager = new SbtPackager();

    BomToolType getBomToolType() {
        return BomToolType.SBT
    }

    boolean isBomToolApplicable() {
        String buildDotSbt = detectFileManager.findFile(sourcePath, BUILD_SBT_FILENAME)
        boolean reportsExist = detectFileManager.directoryExists(sourcePath, REPORT_FILE_DIRECTORY)

        if (buildDotSbt && reportsExist) {
            return true
        } else if (buildDotSbt) {
            logger.warn("This is an sbt project but no artifacts were detected at : ${REPORT_FILE_DIRECTORY}")
            return false
        } else {
            return false
        }
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        String included = detectConfiguration.getSbtIncludedConfigurationNames();
        String excluded = detectConfiguration.getSbtExcludedConfigurationNames();

        int depth = detectConfiguration.getSearchDepth();
        List<File> sbtFiles = detectFileManager.findFilesToDepth(sourcePath, BUILD_SBT_FILENAME, depth)

        DependencyNode root = null;
        List<DependencyNode> children = new ArrayList<DependencyNode>();

        sbtFiles.each { sbtFile ->
            def sbtDirectory = sbtFile.getParentFile();
            def reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY);

            List<File> reportFiles = detectFileManager.findFiles(reportPath, REPORT_FILE_PATTERN);

            DependencyNode node = sbtPackager.makeDependencyNode(reportFiles, included, excluded);

            if (node == null) {
                logger.warn("No dependencies could be generated for report folder: ${reportPath}")
            } else {
                if (sbtDirectory.path.equals(sourcePath)) {
                    root = node;
                } else {
                    children.add(node);
                }
            }
        }

        if (root == null) {
            logger.error("Unable to find dependencies for the root artifact.");
            return []
        } else {
            root.children.addAll(children);

            def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, root)

            return [detectCodeLocation]
        }
    }
}