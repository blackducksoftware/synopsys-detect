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
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class FinalStepJsonProtoHaskellCabalLibraries implements FinalStep {
    private static final String FORGE_NAME = "hackage";
    private static final String FORGE_SEPARATOR = "/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Forge hackageForge = new Forge(FORGE_SEPARATOR, FORGE_NAME);
    private final HaskellCabalLibraryJsonProtoParser parser;
    private final ExternalIdFactory externalIdFactory;

    public FinalStepJsonProtoHaskellCabalLibraries(HaskellCabalLibraryJsonProtoParser parser, ExternalIdFactory externalIdFactory) {
        this.parser = parser;
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public MutableDependencyGraph finish(List<String> input) throws IntegrationException {
        String jsonString = extractJsonString(input);
        List<NameVersion> dependencyList = parser.parse(jsonString);
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (NameVersion dependencyDetails : dependencyList) {
            addDependencyToGraph(dependencyGraph, dependencyDetails);
        }
        return dependencyGraph;
    }

    private String extractJsonString(List<String> input) throws IntegrationException {
        if (input.size() != 1) {
            throw new IntegrationException(String.format("Input size is %d; expected 1", input.size()));
        }
        return input.get(0);
    }

    private void addDependencyToGraph(MutableDependencyGraph dependencyGraph, NameVersion dependencyDetails) throws IntegrationException {
        Dependency artifactDependency = hackageCompNameVersionToDependency(dependencyDetails.getName(), dependencyDetails.getVersion());
        try {
            logger.debug(String.format("Adding %s to graph", artifactDependency.getExternalId().toString()));
            dependencyGraph.addChildToRoot(artifactDependency);
        } catch (Exception e) {
            logger.error(String.format("Unable to create dependency from %s/%s", dependencyDetails.getName(), dependencyDetails.getVersion()));
        }
    }

    private Dependency hackageCompNameVersionToDependency(String compName, String compVersion) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(hackageForge, compName, compVersion);
        externalId.createBdioId(); // Validity check; throws IllegalStateException if invalid
        return new Dependency(compName, compVersion, externalId);
    }
}
