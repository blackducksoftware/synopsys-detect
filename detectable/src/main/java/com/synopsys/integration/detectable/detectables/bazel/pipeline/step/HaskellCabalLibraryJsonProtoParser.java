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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class HaskellCabalLibraryJsonProtoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<NameVersion> parse(String jsonProtoString) throws IntegrationException {
        List<NameVersion> dependencies = new ArrayList<>();
        JsonElement protoElement = JsonParser.parseString(jsonProtoString);
        JsonObject protoObject = protoElement.getAsJsonObject();
        JsonElement resultsElement = protoObject.get("results");
        JsonArray resultsArray = resultsElement.getAsJsonArray();
        for (JsonElement resultsArrayMemberElement : resultsArray) {
            Optional<JsonObject> ruleObject = extractHaskellCabalLibraryRule(resultsArrayMemberElement);
            if (ruleObject.isPresent()) {
                addDependencyToList(dependencies, ruleObject.get());
            }
        }
        return dependencies;
    }

    private void addDependencyToList(List<NameVersion> dependencies, JsonObject ruleObject) throws IntegrationException {
        NameVersion dependencyNameVersion = extractDependencyDetails(ruleObject);
        dependencies.add(dependencyNameVersion);
    }

    private NameVersion extractDependencyDetails(JsonObject ruleObject) throws IntegrationException {
        JsonElement attributeElement = ruleObject.get("attribute");
        JsonArray attributeArray = attributeElement.getAsJsonArray();
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
}
