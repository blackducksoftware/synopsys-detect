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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.exception.IntegrationException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FinalStepJsonProtoHaskellCabalLibraries implements FinalStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;

    public FinalStepJsonProtoHaskellCabalLibraries(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public MutableDependencyGraph finish(final List<String> input) throws IntegrationException {
        final MutableDependencyGraph dependencyGraph  = new MutableMapDependencyGraph();

        final JsonElement resultsElement = JsonParser.parseString(input.get(0));
        final JsonObject resultsObject = resultsElement.getAsJsonObject();
        final JsonElement resultsMember = resultsObject.get("results");
        final JsonArray targets = resultsMember.getAsJsonArray();
        logger.info(String.format("Number of targets: %d", targets.size()));
        for (final JsonElement targetElement : targets) {
            final JsonObject targetObject = targetElement.getAsJsonObject();
            final JsonElement targetElementSub = targetObject.get("target");
            final JsonObject targetObjectSub = targetElementSub.getAsJsonObject();
            logger.debug(String.format("targetType: %s", targetObjectSub.get("type").toString()));
            final JsonElement targetTypeElement = targetObjectSub.get("type");
            final String targetTypeValue = targetTypeElement.getAsString();
            if (!"RULE".equals(targetTypeValue)) {
                logger.info(String.format("This is not a rule; skipping it. (It's a %s)", targetTypeValue));
                continue;
            }
            final JsonElement ruleElement = targetObjectSub.get("rule");
            final JsonObject ruleObject = ruleElement.getAsJsonObject();
            logger.debug(String.format("ruleClass: %s", ruleObject.get("ruleClass").toString()));
            // get the real object and check that ruleClass is haskell_cabal_library
            final JsonElement ruleClassElement = ruleObject.get("ruleClass");
            final String ruleClassValue = ruleClassElement.getAsString();
            if (!"haskell_cabal_library".equals(ruleClassValue)) {
                logger.info(String.format("This is not a haskell_cabal_library rule; skipping it. (It's a %s rule)", ruleClassValue));
                continue;
            }
            final JsonElement attributeElement = ruleObject.get("attribute");
            final JsonArray attributeArray = attributeElement.getAsJsonArray();
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
                    logger.info(String.format("dependencyNameValue: %s", dependencyNameValue));
                }

                if ("version".equals(currentAttributeNameValue)) {
                    final JsonElement dependencyVersionElement = currentAttributeObject.get("stringValue");
                    dependencyVersionValue = dependencyVersionElement.getAsString();
                    logger.info(String.format("dependencyVersionValue: %s", dependencyVersionValue));
                }
                if (dependencyNameValue != null && dependencyVersionValue != null) {
                    final Dependency artifactDependency = haskageCompNameVersionToDependency(dependencyNameValue, dependencyVersionValue);
                    try {
                        logger.info(String.format("Adding %s to graph", artifactDependency.getExternalId().toString()));
                        dependencyGraph.addChildToRoot(artifactDependency);
                    } catch (final Exception e) {
                        logger.error(String.format("Unable to create dependency from %s/%s", dependencyNameValue, dependencyVersionValue));
                    }
                    break;
                }
            }

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
}
