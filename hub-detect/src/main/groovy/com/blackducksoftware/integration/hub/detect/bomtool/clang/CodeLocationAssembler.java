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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class CodeLocationAssembler {
    private final ExternalIdFactory externalIdFactory;

    public CodeLocationAssembler(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DetectCodeLocation generateCodeLocation(final Forge defaultForge, final File rootDir, final List<Dependency> bdioComponents) {
        final MutableDependencyGraph dependencyGraph = populateGraph(bdioComponents);
        final ExternalId externalId = externalIdFactory.createPathExternalId(defaultForge, rootDir.toString());
        return new DetectCodeLocation.Builder(BomToolGroupType.CLANG, BomToolType.CLANG, rootDir.toString(), externalId, dependencyGraph).build();
    }

    private MutableDependencyGraph populateGraph(final List<Dependency> bdioComponents) {
        final MutableDependencyGraph dependencyGraph = new SimpleBdioFactory().createMutableDependencyGraph();
        for (final Dependency bdioComponent : bdioComponents) {
            dependencyGraph.addChildToRoot(bdioComponent);
        }
        return dependencyGraph;
    }
}
