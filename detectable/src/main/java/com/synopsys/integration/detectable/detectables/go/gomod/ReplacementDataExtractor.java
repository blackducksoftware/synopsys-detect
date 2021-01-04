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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListUJsonData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class ReplacementDataExtractor {

    private Gson gson;

    public ReplacementDataExtractor(Gson gson) {
        this.gson = gson;
    }

    public Map<String, String> extractReplacementData(List<String> listUJsonOutput) throws DetectableException {
        // Similar to Extractor A in that we're going to delegate parsing to Gson, but we're only going to convert elements one at a time
        Map<String, String> replacementData = new HashMap<>();
        StringBuilder rawEntry = new StringBuilder();
        for (String line : listUJsonOutput) {
            rawEntry.append(line);
            if (line.startsWith("}")) {
                try {
                    GoListUJsonData data = gson.fromJson(rawEntry.toString(), GoListUJsonData.class);

                    ReplaceData replace = data.getReplace();
                    if (replace != null) {
                        String path = data.getPath();
                        String originalVersion = data.getVersion();
                        String replaceVersion = replace.getVersion();
                        replacementData.put(String.format("%s@%s", path, originalVersion), String.format("%s@%s", path, replaceVersion));
                    }

                    // Reset to accumulate a new entry
                    rawEntry = new StringBuilder();
                } catch (JsonSyntaxException e) {
                    throw new DetectableException(e.getMessage());
                }
            }
        }
        return replacementData;
    }
}
