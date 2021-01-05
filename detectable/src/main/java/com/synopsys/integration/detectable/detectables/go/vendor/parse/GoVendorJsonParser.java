/**
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
package com.synopsys.integration.detectable.detectables.go.vendor.parse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.vendor.model.PackageData;
import com.synopsys.integration.detectable.detectables.go.vendor.model.VendorJson;

public class GoVendorJsonParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GoVendorJsonParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseVendorJson(final Gson gson, final String vendorJsonContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final VendorJson vendorJsonData = gson.fromJson(vendorJsonContents, VendorJson.class);
        logger.trace(String.format("vendorJsonData: %s", vendorJsonData));
        for (final PackageData pkg : vendorJsonData.getPackages()) {
            if (StringUtils.isNotBlank(pkg.getPath()) && StringUtils.isNotBlank(pkg.getRevision())) {
                final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, pkg.getPath(), pkg.getRevision());
                final Dependency dependency = new Dependency(pkg.getPath(), pkg.getRevision(), dependencyExternalId);
                logger.trace(String.format("dependency: %s", dependency.getExternalId().toString()));
                graph.addChildToRoot(dependency);
            } else {
                logger.debug(String.format("Omitting package path:'%s', revision:'%s' (one or both of path, revision is/are missing)", pkg.getPath(), pkg.getRevision()));
            }
        }
        return graph;
    }
}
