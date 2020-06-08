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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FinalStepJsonProtoHaskellCabalLibraries implements FinalStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public MutableDependencyGraph finish(final List<String> input) throws IntegrationException {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        final JsonElement protoElement = JsonParser.parseString(input.get(0));
        final JsonObject protoObject = protoElement.getAsJsonObject();
        final JsonElement resultsElement = protoObject.get("results");
        final JsonArray resultsArray = resultsElement.getAsJsonArray();
        logger.debug(String.format("Number of resultsArray: %d", resultsArray.size()));
        for (final JsonElement resultsArrayMemberElement : resultsArray) {
            final JsonObject resultsArrayMemberObject = resultsArrayMemberElement.getAsJsonObject();
            final JsonElement targetElement = resultsArrayMemberObject.get("target");
            final JsonObject targetObject = targetElement.getAsJsonObject();
            logger.debug(String.format("targetType: %s", targetObject.get("type").toString()));
            final JsonElement targetTypeElement = targetObject.get("type");
            final String targetTypeValue = targetTypeElement.getAsString();
            if (!"RULE".equals(targetTypeValue)) {
                logger.debug(String.format("This is not a rule; skipping it. (It's a %s)", targetTypeValue));
                continue;
            }
            final JsonElement ruleElement = targetObject.get("rule");
            final JsonObject ruleObject = ruleElement.getAsJsonObject();
            logger.debug(String.format("ruleClass: %s", ruleObject.get("ruleClass").toString()));
            final JsonElement ruleClassElement = ruleObject.get("ruleClass");
            final String ruleClassValue = ruleClassElement.getAsString();
            if (!"haskell_cabal_library".equals(ruleClassValue)) {
                logger.debug(String.format("This is not a haskell_cabal_library rule; skipping it. (It's a %s rule)", ruleClassValue));
                continue;
            }
            addDependencyToGraph(dependencyGraph, ruleObject);

        }
        return dependencyGraph;
    }

    public Dependency haskageCompNameVersionToDependency(final String compName, final String compVersion) {
        final Forge hackageForge = new Forge("/", "hackage");
        final ExternalId externalId = (new ExternalIdFactory()).createNameVersionExternalId(hackageForge, compName, compVersion);
        externalId.createBdioId(); // Validity check; throws IllegalStateException if invalid
        final Dependency artifactDependency = new Dependency(compName, compVersion, externalId);
        return artifactDependency;
    }

    private void addDependencyToGraph(final MutableDependencyGraph dependencyGraph, final JsonObject ruleObject) throws IntegrationException {
        final JsonElement attributeElement = ruleObject.get("attribute");
        final JsonArray attributeArray = attributeElement.getAsJsonArray();

        final NameVersion dependencyNameVersion = extractDependencyDetails(attributeArray);
        final Dependency artifactDependency = haskageCompNameVersionToDependency(dependencyNameVersion.getName(), dependencyNameVersion.getVersion());
        try {
            logger.debug(String.format("Adding %s to graph", artifactDependency.getExternalId().toString()));
            dependencyGraph.addChildToRoot(artifactDependency);
        } catch (final Exception e) {
            logger.error(String.format("Unable to create dependency from %s/%s", dependencyNameVersion.getName(), dependencyNameVersion.getVersion()));
        }
    }

    private NameVersion extractDependencyDetails(final JsonArray attributeArray) throws IntegrationException {
        String dependencyNameValue = null;
        String dependencyVersionValue = null;
        for (final JsonElement currentAttribute : attributeArray) {
            final JsonObject currentAttributeObject = currentAttribute.getAsJsonObject();
            final JsonElement currentAttributeNameElement = currentAttributeObject.get("name");
            final String currentAttributeNameValue = currentAttributeNameElement.getAsString();
            logger.trace(String.format("currentAttributeNameElement: %s", currentAttributeNameValue));
            if ("name".equals(currentAttributeNameValue)) {
                final JsonElement dependencyNameElement = currentAttributeObject.get("stringValue");
                dependencyNameValue = dependencyNameElement.getAsString();
                logger.trace(String.format("dependencyNameValue: %s", dependencyNameValue));
            }
            if ("version".equals(currentAttributeNameValue)) {
                final JsonElement dependencyVersionElement = currentAttributeObject.get("stringValue");
                dependencyVersionValue = dependencyVersionElement.getAsString();
                logger.trace(String.format("dependencyVersionValue: %s", dependencyVersionValue));
            }
            if (dependencyNameValue != null && dependencyVersionValue != null) {
                return new NameVersion(dependencyNameValue, dependencyVersionValue);
            }
        }
        throw new IntegrationException(String.format("Did not find dependency name and version in %s", attributeArray.toString()));
    }
}
