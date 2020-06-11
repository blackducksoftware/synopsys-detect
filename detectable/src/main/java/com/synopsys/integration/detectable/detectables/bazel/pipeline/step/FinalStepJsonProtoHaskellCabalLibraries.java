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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    @Override
    public MutableDependencyGraph finish(List<String> input) throws IntegrationException {
        String jsonString = extractJsonString(input);
        JsonArray resultsArray = parseResultsJson(jsonString);
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (JsonElement resultsArrayMemberElement : resultsArray) {
            Optional<JsonObject> ruleObject = extractHaskellCabalLibraryRule(resultsArrayMemberElement);
            if (ruleObject.isPresent()) {
                addDependencyToGraph(dependencyGraph, ruleObject.get());
            }
        }
        return dependencyGraph;
    }

    private String extractJsonString(List<String> input) throws IntegrationException {
        if (input.size() != 1) {
            throw new IntegrationException(String.format("Input size is %d; expected 1", input.size()));
        }
        return input.get(0);
    }

    private JsonArray parseResultsJson(String jsonString) {
        JsonElement protoElement = JsonParser.parseString(jsonString);
        JsonObject protoObject = protoElement.getAsJsonObject();
        JsonElement resultsElement = protoObject.get("results");
        return resultsElement.getAsJsonArray();
    }

    private Dependency hackageCompNameVersionToDependency(String compName, String compVersion) {
        ExternalId externalId = (new ExternalIdFactory()).createNameVersionExternalId(hackageForge, compName, compVersion);
        externalId.createBdioId(); // Validity check; throws IllegalStateException if invalid
        return new Dependency(compName, compVersion, externalId);
    }

    private Optional<JsonObject> extractHaskellCabalLibraryRule(JsonElement resultsArrayMemberElement) {
        JsonObject resultsArrayMemberObject = resultsArrayMemberElement.getAsJsonObject();
        JsonElement targetElement = resultsArrayMemberObject.get("target");
        JsonObject targetObject = targetElement.getAsJsonObject();
        logger.debug(String.format("targetType: %s", targetObject.get("type").toString()));
        JsonElement targetTypeElement = targetObject.get("type");
        String targetTypeValue = targetTypeElement.getAsString();
        if (!"RULE".equals(targetTypeValue)) {
            logger.debug(String.format("This is not a rule; skipping it. (It's a %s)", targetTypeValue));
            return Optional.empty();
        }
        JsonElement ruleElement = targetObject.get("rule");
        JsonObject ruleObject = ruleElement.getAsJsonObject();
        logger.debug(String.format("ruleClass: %s", ruleObject.get("ruleClass").toString()));
        JsonElement ruleClassElement = ruleObject.get("ruleClass");
        String ruleClassValue = ruleClassElement.getAsString();
        if (!"haskell_cabal_library".equals(ruleClassValue)) {
            logger.debug(String.format("This is not a haskell_cabal_library rule; skipping it. (It's a %s rule)", ruleClassValue));
            return Optional.empty();
        }
        return Optional.of(ruleObject);
    }

    private void addDependencyToGraph(MutableDependencyGraph dependencyGraph, JsonObject ruleObject) throws IntegrationException {
        JsonElement attributeElement = ruleObject.get("attribute");
        JsonArray attributeArray = attributeElement.getAsJsonArray();

        NameVersion dependencyNameVersion = extractDependencyDetails(attributeArray);
        Dependency artifactDependency = hackageCompNameVersionToDependency(dependencyNameVersion.getName(), dependencyNameVersion.getVersion());
        try {
            logger.debug(String.format("Adding %s to graph", artifactDependency.getExternalId().toString()));
            dependencyGraph.addChildToRoot(artifactDependency);
        } catch (Exception e) {
            logger.error(String.format("Unable to create dependency from %s/%s", dependencyNameVersion.getName(), dependencyNameVersion.getVersion()));
        }
    }

    private NameVersion extractDependencyDetails(JsonArray attributeArray) throws IntegrationException {
        String dependencyNameValue = null;
        String dependencyVersionValue = null;
        for (JsonElement currentAttribute : attributeArray) {
            JsonObject currentAttributeObject = currentAttribute.getAsJsonObject();
            JsonElement currentAttributeNameElement = currentAttributeObject.get("name");
            String currentAttributeNameValue = currentAttributeNameElement.getAsString();
            logger.trace(String.format("currentAttributeNameElement: %s", currentAttributeNameValue));
            if ("name".equals(currentAttributeNameValue)) {
                JsonElement dependencyNameElement = currentAttributeObject.get("stringValue");
                dependencyNameValue = dependencyNameElement.getAsString();
                logger.trace(String.format("dependencyNameValue: %s", dependencyNameValue));
            }
            if ("version".equals(currentAttributeNameValue)) {
                JsonElement dependencyVersionElement = currentAttributeObject.get("stringValue");
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
