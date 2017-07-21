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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType

import groovy.util.slurpersupport.GPathResult

@Component
class SbtBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(SbtBomTool.class)

    static final String BUILD_SBT_FILENAME = 'build.sbt'
    static final String REPORT_FILE_DIRECTORY = "${File.separator}target${File.separator}resolution-cache${File.separator}reports"
    static final String REPORT_FILE_PATTERN = '*.xml'

    static final String REPORT_DIRECTORY_DEPTH = 2

    @Autowired
    SbtPackager sbtPackager

    private String mvnExecutable

    BomToolType getBomToolType() {
        return BomToolType.SBT
    }

    boolean isBomToolApplicable() {
        String buildDotSbt = detectFileManager.findFile(sourcePath, BUILD_SBT_FILENAME)

        buildDotSbt
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {

        def included = detectConfiguration.getSbtIncludedConfigurationNames();
        def excluded = detectConfiguration.getSbtExcludedConfigurationNames();

        def depth = detectConfiguration.getSearchDepth();
        List<File> sbtFiles = detectFileManager.findFilesToDepth(sourcePath, BUILD_SBT_FILENAME, depth)

        DependencyNode root;
        List<DependencyNode> children = new ArrayList<DependencyNode>();

        sbtFiles.each { sbtFile ->
            def sbtDirectory = sbtFile.getParentFile();
            def reportPath = new File(sbtDirectory, REPORT_FILE_DIRECTORY);

            List<File> reportFiles = detectFileManager.findFiles(reportPath, REPORT_FILE_PATTERN);

            List<GPathResult> xmls = reportFiles.collect { reportFile ->
                def text = reportFile.text;
                def xml = new XmlSlurper().parseText(text)
                xml
            }

            DependencyNode node = sbtPackager.makeDependencyNode(xmls, included, excluded);
            if (sbtDirectory.path.equals(sourcePath)){
                root = node;
            }else{
                children.add(node);
            }
        }

        root.children.addAll(children);

        DetectCodeLocation detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, root)

        [detectCodeLocation]
    }

    DependencyNode extractSbtNode() {
    }
}