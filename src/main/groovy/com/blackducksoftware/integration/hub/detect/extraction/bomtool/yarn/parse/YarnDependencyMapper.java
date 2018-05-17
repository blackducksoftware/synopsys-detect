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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class YarnDependencyMapper extends BaseYarnParser {

    private final Map<String, String> resolvedVersions = new HashMap<>();

    public void getYarnDataAsMap(List<String> inputLines) {
        List<String> thisDependency = new ArrayList<>();
        String thisVersion;

        for (String line : inputLines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("#")) {
                continue;
            }

            int level = getLineLevel(line);
            if (level == 0) {
                thisDependency = cleanAndSplit(line);
                continue;
            }

            if (level == 1 && trimmedLine.startsWith("version")) {
                thisVersion = trimmedLine.split(" ")[1].replaceAll("\"", "");
                for (String dep : thisDependency) {
                    resolvedVersions.put(dep, thisVersion);
                }
                resolvedVersions.put(thisDependency.get(0).split("@")[0] + "@" + thisVersion, thisVersion);
            }
        }
    }

    public Optional<String> getVersion(String key) {
        if (resolvedVersions.containsKey(key)) {
            String value = resolvedVersions.get(key);
            if (StringUtils.isNotBlank(value)) {
                return Optional.of(value);
            } else {
                return Optional.empty();
            }
        } else {
            String name = key.split("@")[0];
            for (String fuzzy : resolvedVersions.keySet()) {
                String fullResolvedName = name + "@" + resolvedVersions.get(fuzzy);
                boolean versionHasAlreadyBeenResolvedByYarnList = fuzzy.equals(fullResolvedName);
                if (versionHasAlreadyBeenResolvedByYarnList) {
                    String value = resolvedVersions.get(fuzzy);
                    if (StringUtils.isNotBlank(value)) {
                        return Optional.of(value);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Map<String, String> getResolvedVersions() {
        return resolvedVersions;
    }

    private List<String> cleanAndSplit(String s) {
        List<String> lines = Arrays.asList(s.split(","));
        List<String> result = new ArrayList<>();

        for (String l : lines) {
            result.add(l.trim().replaceAll("\"", "").replaceAll(":", ""));
        }

        return result;
    }

}
