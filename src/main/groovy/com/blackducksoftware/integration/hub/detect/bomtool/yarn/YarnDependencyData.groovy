/*
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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn

class YarnDependencyData {

    static Map<String, String> resolvedVersions

    static Map<String, String> getYarnDataAsMap(List<String> inputLines) {
        resolvedVersions = new HashMap<>()

        String thisDependency = ""
        String thisVersion

        for (String line : inputLines) {
            if (!line.trim()) {
                continue
            }

            if (line.trim().startsWith('#')) {
                continue
            }

            int level = YarnPackager.getLineLevel(line)
            if (level == 0) {
                thisDependency = line.trim().replace("\"", "").replaceAll(":", "")
                continue
            }

            if (level == 1 && line.trim().startsWith('version')) {
                thisVersion = line.trim().split(' ')[1].replaceAll('"', '')
                resolvedVersions.put(thisDependency, thisVersion)
                resolvedVersions.put(thisDependency.split("@")[0] + "@" + thisVersion, thisVersion)
            }
        }
    }

    static String getVersion(String key) {
        if (resolvedVersions.containsKey(key)) {

            return resolvedVersions.get(key)

        } else {
            String name = key.split("@")[0]
            for (String fuzzy in resolvedVersions.keySet()) {
                String fullResolvedName = name + "@" + resolvedVersions.get(fuzzy)
                boolean versionHasAlreadyBeenResolvedByYarnList = fuzzy == fullResolvedName

                if (versionHasAlreadyBeenResolvedByYarnList) {
                    return resolvedVersions.get(fuzzy)
                }

            }
        }
    }


}
