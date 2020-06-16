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
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListUJsonData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class GoModCliExtractor {
    private final ExecutableRunner executableRunner;
    private final GoModGraphParser goModGraphParser;
    private final Gson gson = BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().setLenient().create();
    private final Map<String, String> replacementData = new HashMap<>();

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

        Type goListUJsonEntryType = new TypeToken<List<GoListUJsonData>>() {}.getType();
        List<GoListUJsonData> data = gson.fromJson(jsonString, goListUJsonEntryType);

        for (final GoListUJsonData entry : data) {
            ReplaceData replace = entry.getReplace();
            if (replace != null) {
                String path = entry.getPath();
                String originalVersion = entry.getVersion();
                String replaceVersion = replace.getVersion();
                replacementData.put(String.format("%s@%s", path, originalVersion), String.format("%s@%s", path, replaceVersion));
            }
        }

        for (String line : modGraphOutput) {
            for (String original : replacementData.keySet()) {
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
        stringBuilder.append("[" + System.lineSeparator());
        stringBuilder.append(goModGraphAsString.substring(0, lastCloseBrace));
        stringBuilder.append("}");
        stringBuilder.append(goModGraphAsString.substring(lastCloseBrace + 2));
        stringBuilder.append(System.lineSeparator() + "]");

        return stringBuilder.toString();
    }
}
