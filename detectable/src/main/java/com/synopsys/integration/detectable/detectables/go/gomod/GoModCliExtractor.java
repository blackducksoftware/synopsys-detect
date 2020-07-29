/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class GoModCliExtractor {
    private final ExecutableRunner executableRunner;
    private final GoModGraphParser goModGraphParser;
    private final Gson gson = BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().setLenient().create();

    private final ReplacementDataExtractor replacementDataExtractor = new ReplacementDataExtractor(gson);

    public GoModCliExtractor(ExecutableRunner executableRunner, GoModGraphParser goModGraphParser) {
        this.executableRunner = executableRunner;
        this.goModGraphParser = goModGraphParser;
    }

    public Extraction extract(File directory, File goExe) {
        try {
            List<String> listOutput = execute(directory, goExe, "Querying go for the list of modules failed: ", "list", "-m");
            List<String> listUJsonOutput = execute(directory, goExe, "Querying for the go mod graph failed:", "list", "-m", "-u", "-json", "all");
            List<String> modGraphOutput = modGraphOutputWithReplacements(directory, goExe, listUJsonOutput);
            List<CodeLocation> codeLocations = goModGraphParser.parseListAndGoModGraph(listOutput, modGraphOutput);
            return new Extraction.Builder().success(codeLocations).build();//no project info - hoping git can help with that.
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private List<String> execute(File directory, File goExe, String failureMessage, String... arguments) throws DetectableException, ExecutableRunnerException {
        ExecutableOutput output = executableRunner.execute(directory, goExe, arguments);

        if (output.getReturnCode() == 0) {
            return output.getStandardOutputAsList();
        } else {
            throw new DetectableException(failureMessage + output.getReturnCode());
        }
    }

    private List<String> modGraphOutputWithReplacements(File directory, File goExe, List<String> listUJsonOutput) throws ExecutableRunnerException, DetectableException {
        List<String> modGraphOutput = execute(directory, goExe, "Querying for the go mod graph failed:", "mod", "graph");

        Map<String, String> replacementData = replacementDataExtractor.extractReplacementData(listUJsonOutput);

        for (String line : modGraphOutput) {
            for (String original : replacementData.keySet()) {
                String newLine = line.replace(original, replacementData.get(original));
                int indexOfLine = modGraphOutput.indexOf(line);
                if (!line.equals(newLine)) {
                    modGraphOutput.set(indexOfLine, newLine);
                }
            }
        }
        return modGraphOutput;
    }
}
