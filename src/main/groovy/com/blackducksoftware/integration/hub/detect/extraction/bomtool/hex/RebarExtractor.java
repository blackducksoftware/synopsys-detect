/**
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex.parse.Rebar3TreeParser;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex.parse.RebarParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class RebarExtractor extends Extractor<RebarContext> {

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableRunner executableRunner;

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    Rebar3TreeParser rebarTreeParser;

    @Override
    public Extraction extract(final RebarContext context) {
        try {
            final List<DetectCodeLocation> codeLocations = new ArrayList<>();

            final Map<String, String> envVars = new HashMap<>();
            envVars.put("REBAR_COLOR", "none");

            final List<String> arguments = new ArrayList<>();
            arguments.add("tree");

            final Executable rebar3TreeExe = new Executable(context.directory, envVars, context.rebarExe.toString(), arguments);
            final List<String> output = executableRunner.execute(rebar3TreeExe).getStandardOutputAsList();
            final RebarParseResult parseResult = rebarTreeParser.parseRebarTreeOutput(output, context.directory.toString());

            codeLocations.add(parseResult.codeLocation);

            return new Extraction.Builder().success(codeLocations).projectName(parseResult.projectName).projectVersion(parseResult.projectVersion).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
