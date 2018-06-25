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
package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.google.gson.Gson;

public class GodepsParser {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private static final String UNCORRECTED_VERSION_REGEX = "-\\d+-g[0-9a-f]+$";

    public GodepsParser(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
    }

    public DependencyGraph extractProjectDependencies(final String godepContents) {
        final GodepsFile godepsFile = gson.fromJson(godepContents, GodepsFile.class);
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        godepsFile.getDeps().stream()
                .map(godepDependency -> correctVersion(godepDependency))
                .map(godepDependency -> constructDependency(godepDependency))
                .forEach(dependency -> graph.addChildrenToRoot(dependency));

        return graph;
    }

    private GodepDependency correctVersion(final GodepDependency godepDependency) {
        String version = "";

        if (StringUtils.isNotBlank(godepDependency.getComment())) {
            version = godepDependency.getComment().trim();
            // TODO test with kubernetes

            // https://github.com/blackducksoftware/hub-detect/issues/237
            // updating according to 'git describe'
            if (version.matches(".*" + UNCORRECTED_VERSION_REGEX)) {
                // v1.0.0-10-gae3452 should be changed to v1.0.0
                version = version.replaceAll(UNCORRECTED_VERSION_REGEX, "");
            }
        } else {
            version = godepDependency.getRev().trim();
        }

        godepDependency.setRev(version);
        return godepDependency;
    }

    private Dependency constructDependency(final GodepDependency godepDependency) {
        final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, godepDependency.getImportPath(), godepDependency.getRev());
        return new Dependency(godepDependency.getImportPath(), godepDependency.getRev(), dependencyExternalId);
    }

}
