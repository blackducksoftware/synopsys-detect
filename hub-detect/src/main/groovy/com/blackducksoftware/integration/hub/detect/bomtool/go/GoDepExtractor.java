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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.io.File;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class GoDepExtractor {
    private final DepPackager depPackager;
    private final ExternalIdFactory externalIdFactory;

    public GoDepExtractor(final DepPackager depPackager, final ExternalIdFactory externalIdFactory) {
        this.depPackager = depPackager;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory, final File goExe, final String goDepInspector) {
        try {
            DependencyGraph graph = depPackager.makeDependencyGraph(directory.toString(), goDepInspector);
            if (graph == null) {
                graph = new MutableMapDependencyGraph();
            }

            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, directory.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.GO_DEP, bomToolType, directory.toString(), externalId, graph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
