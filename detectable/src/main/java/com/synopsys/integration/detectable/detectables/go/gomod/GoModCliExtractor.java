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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    private final Map<String, String> replacementData = new HashMap<>();
    private final static String PATHS = "paths";

    public GoModCliExtractor(final ExecutableRunner executableRunner, final GoModGraphParser goModGraphParser) {
        this.executableRunner = executableRunner;
        this.goModGraphParser = goModGraphParser;
    }

    public Extraction extract(final File directory, final File goExe) {
        try {
            final List<String> listOutput = execute(directory, goExe, "Querying go for the list of modules failed: ", "list", "-m");
            final List<String> listUJsonOutput = execute(directory, goExe, "Querying for the go mod graph failed:", "list", "-m", "-u", "-json", "all");
            final List<String> modGraphOutput = modGraphOutputWithReplacements(directory, goExe, listUJsonOutput);
            final List<CodeLocation> codeLocations = goModGraphParser.parseListAndGoModGraph(listOutput, modGraphOutput);
            return new Extraction.Builder().success(codeLocations).build();//no project info - hoping git can help with that.
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private List<String> execute(final File directory, final File goExe, final String failureMessage, final String... arguments) throws DetectableException, ExecutableRunnerException {
        final ExecutableOutput output = executableRunner.execute(directory, goExe, arguments);

        if (output.getReturnCode() == 0) {
            return output.getStandardOutputAsList();
        } else {
            throw new DetectableException(failureMessage + output.getReturnCode());
        }
    }

    private List<String> modGraphOutputWithReplacements(File directory, File goExe, List<String> listUJsonOutput) throws ExecutableRunnerException, DetectableException {
        final List<String> modGraphOutput = execute(directory, goExe, "Querying for the go mod graph failed:", "mod", "graph");
        String jsonString = convertOutputToJsonString(listUJsonOutput);
        JsonObject json = gson.fromJson(jsonString, JsonObject.class);

        for (final JsonElement jsonElement : json.getAsJsonArray(PATHS)) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject replace = jsonObject.getAsJsonObject("Replace");
            if (replace != null) {
                String path = jsonObject.get("Path").getAsString();
                String originalVersion = jsonObject.get("Version").getAsString();
                String replaceVersion = replace.get("Version").getAsString();
                replacementData.put(String.format("%s@%s", path, originalVersion), String.format("%s@%s", path, replaceVersion));
            }
        }

        for (String line : modGraphOutput) {
            for (String original : replacementData.keySet()) {
                if (line.contains("ocsql@v0.1.5")) {
                    System.out.println("");
                }
                String newLine = line.replace(original, replacementData.get(original));
                int indexOfLine = modGraphOutput.indexOf(line);
                modGraphOutput.set(indexOfLine, newLine);
            }
        }
        return modGraphOutput;
    }

    private String convertOutputToJsonString(List<String> listUJsonOutput) {
        // go list -u -json does not provide data in a format that can be consumed by gson
        Collections.replaceAll(listUJsonOutput, "}", "},");
        String goModGraphAsString = String.join(System.lineSeparator(), listUJsonOutput);
        int lastCloseBrace = goModGraphAsString.lastIndexOf("},");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("{%n %s: [%n", PATHS));
        stringBuilder.append(goModGraphAsString.substring(0, lastCloseBrace));
        stringBuilder.append("}");
        stringBuilder.append(goModGraphAsString.substring(lastCloseBrace + 2));
        stringBuilder.append("\n] \n}");

        return stringBuilder.toString();
    }
}
