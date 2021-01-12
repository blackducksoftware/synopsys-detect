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
package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class GemspecParser {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(getClass()));

    private final ExternalIdFactory externalIdFactory;
    private final GemspecLineParser gemspecLineParser;

    public GemspecParser(ExternalIdFactory externalIdFactory, GemspecLineParser gemspecLineParser) {
        this.externalIdFactory = externalIdFactory;
        this.gemspecLineParser = gemspecLineParser;
    }

    public DependencyGraph parse(InputStream inputStream, boolean includeRuntimeDependencies, boolean includeDevelopmentDependencies) throws IOException {
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!gemspecLineParser.shouldParseLine(line)) {
                    continue;
                }

                Optional<GemspecDependency> gemspecDependencyOptional = gemspecLineParser.parseLine(line);
                if (!gemspecDependencyOptional.isPresent()) {
                    continue;
                }

                GemspecDependency gemspecDependency = gemspecDependencyOptional.get();

                if (!includeRuntimeDependencies && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.RUNTIME) {
                    logger.debug(String.format("Excluding component '%s' from graph because it is a runtime dependency", gemspecDependency.getName()));
                    continue;
                } else if (!includeDevelopmentDependencies && gemspecDependency.getGemspecDependencyType() == GemspecDependencyType.DEVELOPMENT) {
                    logger.debug(String.format("Excluding component '%s' from graph because it is a development dependency", gemspecDependency.getName()));
                    continue;
                }
                String name = gemspecDependency.getName();
                String version = gemspecDependency.getVersion().orElse("No version");

                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, name, version);
                Dependency dependency = new Dependency(name, version, externalId);

                dependencyGraph.addChildrenToRoot(dependency);
            }
        }

        return dependencyGraph;
    }
}
