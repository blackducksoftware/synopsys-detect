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
package com.blackducksoftware.integration.hub.detect.bomtool.hex

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HexBomTool extends BomTool<HexApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(HexBomTool.class);

    public static final String REBAR_CONFIG = 'rebar.config';

    @Autowired
    Rebar3TreeParser rebarTreeParser;

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.HEX;
    }

    @Override
    public HexApplicableResult isBomToolApplicable(File directory) {
        def rebarConfig = detectFileManager.findFile(directory, REBAR_CONFIG);

        if (rebarConfig.exists()) {
            logger.info('Rebar3 build tool applies for HEX given the current configuration.');
            def rebarExe = executableManager.findExecutablePath(directory.toString(), ExecutableType.REBAR3, true, detectConfiguration.getHexRebar3Path());
            if (rebarExe) {
                return new HexApplicableResult(directory, rebarConfig, rebarExe);
            } else {
                logger.warn('Could not find a rebar3 executable.');
            }
        }

        return null;
    }

    //#TODO: Bom tool finder - setting project name and version
    @Override
    public BomToolExtractionResult extractDetectCodeLocations(HexApplicableResult applicable) {
        List<DetectCodeLocation> codeLocations = new ArrayList<>();

        Executable rebar3TreeExe = new Executable(applicable.directory, ['REBAR_COLOR': 'none'], applicable.rebarExe, ['tree']);
        List<String> output = executableRunner.execute(rebar3TreeExe).standardOutputAsList;
        RebarParseResult parseResult = rebarTreeParser.parseRebarTreeOutput(output, applicable.directoryString);

        codeLocations.add(parseResult.codeLocation);

        return bomToolExtractionResultsFactory.fromCodeLocations(codeLocations, getBomToolType(), applicable.directory)
    }
}
