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
package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;

public class ConanLockfileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DependencyGraph parse(Gson gson, String conanLockfileContents) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        ConanLockfileData conanLockfileData = gson.fromJson(conanLockfileContents, ConanLockfileData.class);
        logger.trace(String.format("conanLockfileData: %s", conanLockfileData));
        if (!conanLockfileData.getConanLockfileGraph().isRevisionsEnabled()) {
            logger.warn("The Conan revisions feature is not enabled, which will significantly reduce Black Duck's ability to identify dependencies");
        } else {
            logger.trace("The Conan revisions feature is enabled");
        }
        logger.info(String.format("Node 0 path: %s", conanLockfileData.getConanLockfileGraph().getNodeMap().get(0).getPath().get()));
        for (Map.Entry<Integer, ConanLockfileNode> entry : conanLockfileData.getConanLockfileGraph().getNodeMap().entrySet()) {
            logger.info(String.format("%d: %s:%s#%s", entry.getKey(), entry.getValue().getRef().orElse("?"), entry.getValue().getPackageId().orElse("?"), entry.getValue().getPackageRevision().orElse("?")));
        }
        //        for (final PackageData pkg : vendorJsonData.getPackages()) {
        //            if (StringUtils.isNotBlank(pkg.getPath()) && StringUtils.isNotBlank(pkg.getRevision())) {
        //                final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, pkg.getPath(), pkg.getRevision());
        //                final Dependency dependency = new Dependency(pkg.getPath(), pkg.getRevision(), dependencyExternalId);
        //                logger.trace(String.format("dependency: %s", dependency.getExternalId().toString()));
        //                graph.addChildToRoot(dependency);
        //            } else {
        //                logger.debug(String.format("Omitting package path:'%s', revision:'%s' (one or both of path, revision is/are missing)", pkg.getPath(), pkg.getRevision()));
        //            }
        //        }
        return graph;
    }
}
