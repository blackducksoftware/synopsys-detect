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

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.hex.Rebar3TreeParser
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HexBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(HexBomTool.class)

    public static final String REBAR_CONFIG = 'rebar.config'

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    Rebar3TreeParser rebarTreeParser

    private String rebarExePath
    private boolean rebarApplies

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.HEX
    }

    @Override
    public boolean isBomToolApplicable() {
        boolean hasRebarConfig = detectFileManager.containsAllFiles(sourcePath, REBAR_CONFIG)

        if (hasRebarConfig) {
            logger.info('Rebar3 build tool applies for HEX given the current configuration.')
            rebarExePath = findExecutablePath(ExecutableType.REBAR3, true, detectConfiguration.getHexRebar3Path())
            if (!rebarExePath) {
                logger.warn('Could not find a rebar3 executable.')
            }
        }

        rebarApplies = rebarExePath && hasRebarConfig

        return rebarApplies
    }


    @Override
    public List<DetectCodeLocation> extractDetectCodeLocations(DetectProject detectProject) {
        if (rebarApplies) {
            Executable rebar3TreeExe = new Executable(new File(sourcePath), ['REBAR_COLOR': 'none'], rebarExePath, ['tree'])
            List<String> output = executableRunner.execute(rebar3TreeExe).standardOutputAsList
            DetectCodeLocation projectCodeLocation = rebarTreeParser.parseRebarTreeOutput(output, detectProject, sourcePath)

            return [projectCodeLocation]
        }

        []
    }
}
