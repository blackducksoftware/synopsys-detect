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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class ReplacementDataExtractorB {

    private String REPLACE_TOKEN = "\t\"Replace\": {";
    private String PATH_TOKEN = "\t\"Path\": \"";
    private String VERSION_TOKEN = "\t\"Version\": \"";
    private String VERSION_IN_REPLACE_BLOCK_TOKEN = "\t\t\"Version\": \"";

    public Map<String, String> extractReplacementData(List<String> listUJsonOutput) throws DetectableException {
        // Just parse replacement blocks, don't worry about the rest
        Map<String, String> replacementData = new HashMap<>();
        boolean inReplaceBlock = false;
        String path = "";
        String currentVersion = "";
        String replaceVersion = "";
        for (String line : listUJsonOutput) {
            if (!inReplaceBlock && line.startsWith(PATH_TOKEN)) {
                path = extractPathFromLine(line, PATH_TOKEN.length());
            }
            if (!inReplaceBlock && line.startsWith(VERSION_TOKEN)) {
                currentVersion = extractVersionFromLine(line, VERSION_TOKEN.length());
            }
            if (line.startsWith(REPLACE_TOKEN)) {
                inReplaceBlock = true;
            }
            if (inReplaceBlock) {
                if (line.startsWith(VERSION_IN_REPLACE_BLOCK_TOKEN)) {
                    replaceVersion = extractVersionFromLine(line, VERSION_IN_REPLACE_BLOCK_TOKEN.length());
                }
            }
            if (!path.equals("") && !currentVersion.equals("") && !replaceVersion.equals("")) {
                replacementData.put(String.format("%s@%s", path, currentVersion), String.format("%s@%s", path, replaceVersion));
                inReplaceBlock = false;
                path = "";
                currentVersion = "";
                replaceVersion = "";
            }
        }
        return replacementData;
    }

    private String extractPathFromLine(String line, int beginIndex) {
        // Path will always conclude with ",\n"
        return line.substring(beginIndex, line.length() - 2);
    }

    private String extractVersionFromLine(String line, int beginIndex) throws DetectableException {
        int indexOfLastDigit = getIndexOfEndOfVersion(line, beginIndex);
        try {
            return line.substring(beginIndex, indexOfLastDigit);
        } catch (IndexOutOfBoundsException e) {
            throw new DetectableException("Unexpected format in `go list -m -u -json all` output");
        }
    }

    private int getIndexOfEndOfVersion(String line, int beginIndex) {
        for (int index = beginIndex; index < line.length(); index++) {
            // Version will always conclude with terminating '"' since we already skipped the first one
            if (line.charAt(index) == '"') {
                return index;
            }
        }
        return -1;
    }
}
