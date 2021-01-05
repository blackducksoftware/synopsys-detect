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
package com.synopsys.integration.detectable.detectables.sbt.model;

import java.io.File;

import com.synopsys.integration.bdio.graph.DependencyGraph;

public class SbtDependencyModule {
    private File sourcePath;
    private String name;
    private String version;
    private String org;
    private DependencyGraph graph;

    // if this is from a specific configuration
    private String configuration = null;

    public File getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(final File sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(final String org) {
        this.org = org;
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setGraph(final DependencyGraph graph) {
        this.graph = graph;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final String configuration) {
        this.configuration = configuration;
    }
}
