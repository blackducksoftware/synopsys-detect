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

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model.AttributeItem;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model.Proto;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model.ResultItem;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model.Target;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class HaskellCabalLibraryJsonProtoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;

    public HaskellCabalLibraryJsonProtoParser(Gson gson) {
        this.gson = gson;
    }

    public List<NameVersion> parse(String jsonProtoString) throws IntegrationException {
        List<NameVersion> dependencies = new ArrayList<>();
        Proto proto = gson.fromJson(jsonProtoString, Proto.class);
        if (proto == null || proto.getResults() == null || proto.getResults().isEmpty()) {
            throw new IntegrationException(String.format("Unable to parse results from JSON proto string: %s", jsonProtoString));
        }
        for (ResultItem result : proto.getResults()) {
            if (result == null || result.getTarget() == null) {
                throw new IntegrationException(String.format("Unable to parse target from result inJSON proto string: %s", jsonProtoString));
            }
            Target target = result.getTarget();
            if ("RULE".equals(target.getType())) {
                if (target.getRule() == null || target.getRule().getAttribute() == null) {
                    throw new IntegrationException(String.format("Unable to parse attributes from rule inJSON proto string: %s", jsonProtoString));
                }
                List<AttributeItem> attributes = target.getRule().getAttribute();
                Optional<NameVersion> dependency = extractDependency(attributes);
                if (dependency.isPresent()) {
                    logger.debug(String.format("Adding dependency %s/%s", dependency.get().getName(), dependency.get().getVersion()));
                    dependencies.add(dependency.get());
                } else {
                    logger.debug(String.format("No dependency was extractable from attributes: %s", attributes.toString()));
                }
            }
        }
        return dependencies;
    }

    private Optional<NameVersion> extractDependency(List<AttributeItem> attributes) throws IntegrationException {
        String dependencyName = null;
        String dependencyVersion = null;
        for (AttributeItem attributeItem : attributes) {
            if ("name".equals(attributeItem.getName())) {
                dependencyName = attributeItem.getStringValue();
            } else if ("version".equals(attributeItem.getName())) {
                dependencyVersion = attributeItem.getStringValue();
            }
            if (dependencyName != null && dependencyVersion != null) {
                NameVersion dependencyNameVersion = new NameVersion(dependencyName, dependencyVersion);
                return Optional.of(dependencyNameVersion);
            }
        }
        throw new IntegrationException(String.format("Dependency name/version not found in attribute list: %s", attributes.toString()));
    }
}
