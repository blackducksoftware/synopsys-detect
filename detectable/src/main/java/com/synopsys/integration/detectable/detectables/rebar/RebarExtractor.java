/**
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

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.rebar.model.RebarParseResult;
import com.synopsys.integration.detectable.detectables.rebar.parse.Rebar3TreeParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.Executable;

public class RebarExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final Rebar3TreeParser rebarTreeParser;

    public RebarExtractor(final DetectableExecutableRunner executableRunner, final Rebar3TreeParser rebarTreeParser) {
        this.executableRunner = executableRunner;
        this.rebarTreeParser = rebarTreeParser;
    }

    public Extraction extract(final File directory, final File rebarExe) {
        try {
            final List<CodeLocation> codeLocations = new ArrayList<>();

            final Map<String, String> envVars = new HashMap<>();
            envVars.put("REBAR_COLOR", "none");

            final List<String> arguments = new ArrayList<>();
            arguments.add("tree");

            final Executable rebar3TreeExe = Executable.create(directory, envVars, rebarExe.toString(), arguments);
            final List<String> output = executableRunner.execute(rebar3TreeExe).getStandardOutputAsList();
            final RebarParseResult parseResult = rebarTreeParser.parseRebarTreeOutput(output);

            codeLocations.add(parseResult.getCodeLocation());

            final Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
            parseResult.getProjectNameVersion().ifPresent(projectNameVersion -> builder.projectName(projectNameVersion.getName()).projectVersion(projectNameVersion.getVersion()));
            return builder.build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
