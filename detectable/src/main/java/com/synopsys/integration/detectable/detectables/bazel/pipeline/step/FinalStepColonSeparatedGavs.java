/*
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
package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.exception.IntegrationException;

public class FinalStepColonSeparatedGavs implements FinalStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public FinalStepColonSeparatedGavs(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public List<Dependency> finish(List<String> gavStrings) throws IntegrationException {
        List<Dependency> dependencies = new ArrayList<>();
        for (String gavString : gavStrings) {
            Dependency artifactDependency = gavStringToDependency(gavString, ":");
            try {
                dependencies.add(artifactDependency);
            } catch (Exception e) {
                logger.error(String.format("Unable to create dependency from %s", gavString));
            }
        }
        return dependencies;
    }

    private Dependency gavStringToDependency(String artifactString, String separatorRegex) {
        String[] gavParts = artifactString.split(separatorRegex);
        String group = gavParts[0];
        String artifact = gavParts[1];
        String version = gavParts[2];

        logger.debug(String.format("Adding dependency from external id: %s:%s:%s", group, artifact, version));
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }
}
