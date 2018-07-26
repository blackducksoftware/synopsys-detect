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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class YarnDependencyMapper extends BaseYarnParser {
    private final Map<String, String> resolvedVersions = new HashMap<>();

    public void getYarnDataAsMap(final List<String> inputLines) {
        List<String> thisDependency = new ArrayList<>();
        String thisVersion;

        for (final String line : inputLines) {
            if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                continue;
            }

            final String trimmedLine = line.trim();
            final int level = getLineLevel(line);
            if (level == 0) {
                thisDependency = cleanAndSplit(line);
                continue;
            }

            if (level == 1 && trimmedLine.startsWith("version")) {
                thisVersion = trimmedLine.split(" ")[1].replaceAll("\"", "");
                for (final String dep : thisDependency) {
                    resolvedVersions.put(dep, thisVersion);
                }
                resolvedVersions.put(thisDependency.get(0).split("@")[0] + "@" + thisVersion, thisVersion);
            }
        }
    }

    public Optional<String> getVersion(final String key) {
        String version = null;
        if (resolvedVersions.containsKey(key)) {
            version = resolvedVersions.get(key);
        } else {
            final String name = key.split("@")[0];
            for (final String fuzzy : resolvedVersions.keySet()) {
                final String fullResolvedName = name + "@" + resolvedVersions.get(fuzzy);
                final boolean versionHasAlreadyBeenResolvedByYarnList = fuzzy.equals(fullResolvedName);
                if (versionHasAlreadyBeenResolvedByYarnList) {
                    version = resolvedVersions.get(fuzzy);
                    break;
                }
            }
        }
        return Optional.ofNullable(version);
    }

    public Map<String, String> getResolvedVersions() {
        return resolvedVersions;
    }

    private List<String> cleanAndSplit(final String s) {
        final List<String> lines = Arrays.asList(s.split(","));
        final List<String> result = new ArrayList<>();

        for (final String l : lines) {
            result.add(l.trim().replaceAll("\"", "").replaceAll(":", ""));
        }

        return result;
    }

}
