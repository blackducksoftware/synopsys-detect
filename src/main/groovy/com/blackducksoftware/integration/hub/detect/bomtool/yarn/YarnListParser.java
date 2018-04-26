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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

@Component
public class YarnListParser {
    private final Logger logger = LoggerFactory.getLogger(YarnBomTool.class);

    public DependencyGraph parseYarnList(List<String> yarnLockText, List<String> yarnListAsList) {
        YarnDependencyMapper yarnDependencyMapper = new YarnDependencyMapper();
        yarnDependencyMapper.getYarnDataAsMap(yarnLockText);

        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        ExternalId extId = new ExternalId(Forge.NPM);
        UUID randomUUID = UUID.randomUUID();
        String rootName = String.format("detectRootNode - %s", randomUUID);
        extId.name = rootName;

        int depth;
        Dependency parentDep = null;
        for (String line : yarnListAsList) {

            if (line.toLowerCase().startsWith("yarn list") || line.toLowerCase().startsWith("done in") || line.toLowerCase().startsWith("warning")) {
                continue;
            }

            line = line.replaceAll("├─", " ").replaceAll("│", " ").replaceAll("└─", " ");
            depth = getDepth(line);

            if (depth == 0) {
                Optional<Dependency> optionalDependency = getDependencyFromLine(line, yarnDependencyMapper);
                if (optionalDependency.isPresent()) {
                    Dependency currentDep = optionalDependency.get();
                    graph.addChildToRoot(currentDep);
                    parentDep = currentDep;
                } else {
                    continue;
                }
            }

            if (depth >= 1) {
                Optional<Dependency> optionalDependency = getDependencyFromLine(line, yarnDependencyMapper);
                if (optionalDependency.isPresent()) {
                    Dependency currentDep = optionalDependency.get();
                    logger.debug(currentDep.name + "@" + currentDep.version + " is being added as a child of " + parentDep.name + "@" + parentDep.version);
                    graph.addChildWithParent(currentDep, parentDep);
                } else {
                    continue;
                }
            }
        }

        return graph;
    }

    private int getDepth(String line) {
        // how many spaces (S) does it start with? then depth, in this case is, D = (S - 2)/3
        int count = 0;
        String tmpLine = line;
        while (tmpLine.startsWith(" ")) {
            tmpLine = tmpLine.replaceFirst(" ", "");
            count++;
        }

        return Math.floorDiv(count - 2, 3);
    }

    private String grabFuzzyName(String line) {
        // e.g.
        // ├─ whatwg-url@4.8.0 >> whatwg-url@4.8.0
        // OR
        // │  ├─ tr46@~0.0.3 >> tr46@~0.0.3

        // [a-zA-Z\d-]+@.+[\dx]$
        Pattern pattern = Pattern.compile("[ \\d.\\-_a-zA-Z]+@.+");
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        String result = matcher.group(0).trim();

        return result;
    }

    private Optional<Dependency> getDependencyFromLine(String line, YarnDependencyMapper yarnDependencyMapper) {
        String fuzzyName = grabFuzzyName(line);
        String name = fuzzyName.split("@")[0];
        Optional<String> optionalVersion = yarnDependencyMapper.getVersion(fuzzyName);

        if (optionalVersion.isPresent()) {
            String version = optionalVersion.get();
            logger.debug("Found version " + version + " for " + fuzzyName);

            ExternalId extId = new ExternalId(Forge.NPM);
            extId.name = name;
            extId.version = version;

            return Optional.of(new Dependency(name, version, extId));
        } else {
            logger.error(String.format("Could not determine a version for yarn dependency %s", name));
            return Optional.empty();
        }
    }
}
