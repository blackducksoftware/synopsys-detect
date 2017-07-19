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
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
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

        File reportPath = new File(sourcePath, REPORT_FILE_DIRECTORY);
        List<File> files = detectFileManager.findFiles(reportPath, REPORT_FILE_PATTERN);

        def included = detectConfiguration.getSbtIncludedConfigurationNames();
        def excluded = detectConfiguration.getSbtExcludedConfigurationNames();

        List<GPathResult> xmls = files.each { file ->
            def text = file.text;
            def xml = new XmlSlurper().parseText(file.text)
        }

        DependencyNode node = sbtPackager.makeDependencyNode(xmls, included, excluded);

        DetectCodeLocation detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, node)

        [detectCodeLocation]
    }
}