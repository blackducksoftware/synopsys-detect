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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class BazelBdioBuilder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;
    private final MutableDependencyGraph dependencyGraph;
    private File workspaceDir;

    public BazelBdioBuilder(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
        dependencyGraph = new MutableMapDependencyGraph();
    }

    public BazelBdioBuilder setWorkspaceDir(final File workspaceDir) {
        this.workspaceDir = workspaceDir;
        return this;
    }

    public BazelBdioBuilder addDependency(final BazelExternalId bazelExternalId) {
        try {
            logger.debug(String.format("Adding dependency from external id: %s", bazelExternalId));
            // TODO: always creating a maven externalId may become too limiting
            final ExternalId externalId = externalIdFactory.createMavenExternalId(bazelExternalId.getGroup(), bazelExternalId.getArtifact(), bazelExternalId.getVersion());
            Dependency artifactDependency = new Dependency(bazelExternalId.getArtifact(), bazelExternalId.getVersion(), externalId);
            dependencyGraph.addChildToRoot(artifactDependency);
        } catch (Exception e) {
            logger.error(String.format("Unable to parse group:artifact:version from %s", bazelExternalId));
        }
        return this;
    }

    public List<DetectCodeLocation> build() {
        Dependency projectDependency = gavToProject("unknown", workspaceDir.getName(), "unknown");
        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.MAVEN, workspaceDir.getAbsolutePath(),
            projectDependency.externalId, dependencyGraph).build();
        List<DetectCodeLocation> codeLocations = new ArrayList<>(1);
        codeLocations.add(codeLocation);
        return codeLocations;
    }

    private Dependency gavToProject(final String group, final String artifact, final String version) {
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }
}
