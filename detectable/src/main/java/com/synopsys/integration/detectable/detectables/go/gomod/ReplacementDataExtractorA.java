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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListUJsonData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class ReplacementDataExtractorA {

    private Map<String, String> replacementData;
    private Gson gson;

    public ReplacementDataExtractorA(Map<String, String> replacementData, Gson gson) {
        this.replacementData = replacementData;
        this.gson = gson;
    }

    public void extractReplacementData(List<String> listUJsonOutput) throws DetectableException {
        String jsonString = convertOutputToJsonString(listUJsonOutput);

        Type goListUJsonEntryType = new TypeToken<List<GoListUJsonData>>() {}.getType();

        try {
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
        } catch (JsonSyntaxException e) {
            throw new DetectableException(e.getMessage());
        }
    }

    private String convertOutputToJsonString(List<String> listUJsonOutput) {
        // go list -u -json does not provide data in a format that can be consumed by gson
        Collections.replaceAll(listUJsonOutput, "}", "},");
        String goModGraphAsString = String.join(System.lineSeparator(), listUJsonOutput);
        int lastCloseBrace = goModGraphAsString.lastIndexOf("},");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[" + System.lineSeparator());
        stringBuilder.append(goModGraphAsString, 0, lastCloseBrace);
        stringBuilder.append("}");
        stringBuilder.append(goModGraphAsString.substring(lastCloseBrace + 2));
        stringBuilder.append(System.lineSeparator() + "]");

        return stringBuilder.toString();
    }
}
