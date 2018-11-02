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
package com.blackducksoftware.integration.hub.detect.detector.go;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class VndrParser {
    public ExternalIdFactory externalIdFactory;

    public VndrParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseVendorConf(final List<String> vendorConfContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        // TODO test against moby
        vendorConfContents.forEach(line -> {
            if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
                final String[] parts = line.split(" ");

                final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, parts[0], parts[1]);
                final Dependency dependency = new Dependency(parts[0], parts[1], dependencyExternalId);
                graph.addChildToRoot(dependency);
            }
        });

        return graph;
    }

}
