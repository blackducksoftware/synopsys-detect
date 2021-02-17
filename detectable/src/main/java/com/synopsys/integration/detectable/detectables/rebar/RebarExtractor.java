/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.rebar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.rebar.model.RebarParseResult;
import com.synopsys.integration.detectable.detectables.rebar.parse.Rebar3TreeParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;

public class RebarExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final Rebar3TreeParser rebarTreeParser;

    public RebarExtractor(DetectableExecutableRunner executableRunner, Rebar3TreeParser rebarTreeParser) {
        this.executableRunner = executableRunner;
        this.rebarTreeParser = rebarTreeParser;
    }

    public Extraction extract(File directory, ExecutableTarget rebarExe) {
        try {
            List<CodeLocation> codeLocations = new ArrayList<>();

            Map<String, String> envVars = new HashMap<>();
            envVars.put("REBAR_COLOR", "none");

            List<String> arguments = new ArrayList<>();
            arguments.add("tree");

            Executable rebar3TreeExe = ExecutableUtils.createFromTarget(directory, envVars, rebarExe, arguments);
            List<String> output = executableRunner.execute(rebar3TreeExe).getStandardOutputAsList();
            RebarParseResult parseResult = rebarTreeParser.parseRebarTreeOutput(output);

            codeLocations.add(parseResult.getCodeLocation());

            Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
            parseResult.getProjectNameVersion().ifPresent(projectNameVersion -> builder.projectName(projectNameVersion.getName()).projectVersion(projectNameVersion.getVersion()));
            return builder.build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
