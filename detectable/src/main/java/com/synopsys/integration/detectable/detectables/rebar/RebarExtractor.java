/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
