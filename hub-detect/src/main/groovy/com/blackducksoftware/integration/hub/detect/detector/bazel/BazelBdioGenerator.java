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
package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class BazelBdioGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public BazelBdioGenerator(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Optional<Dependency> artifactStringToDependency(final String artifactString, final String artifactStringSeparatorRegex) {
        try {
            final String[] gavParts = artifactString.split(artifactStringSeparatorRegex);
            final String group = gavParts[0];
            final String artifact = gavParts[1];
            final String version = gavParts[2];
            // TODO: always creating a maven externalId may become too limiting
            final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
            return Optional.of(new Dependency(artifact, version, externalId));
        } catch (Exception e) {
            logger.error(String.format("Unable to parse group:artifact:version from %s", artifactString));
            return Optional.empty();
        }
    }
}
