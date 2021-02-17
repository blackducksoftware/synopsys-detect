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
package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class NpmDependency {
    private final String name;
    private final String version;
    private final boolean devDependency;
    private final Dependency dependency;

    public NpmDependency(final String name, final String version, final boolean devDependency, final Dependency dependency) {
        this.name = name;
        this.version = version;
        this.devDependency = devDependency;
        this.dependency = dependency;
    }

    private NpmDependency parent;
    private final List<NpmRequires> requires = new ArrayList<>();
    private final List<NpmDependency> dependencies = new ArrayList<>();

    public Optional<NpmDependency> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(final NpmDependency parent) {
        this.parent = parent;
    }

    public void addAllRequires(final Collection<NpmRequires> required) {
        this.requires.addAll(required);
    }

    public void addAllDependencies(final Collection<NpmDependency> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    public List<NpmRequires> getRequires() {
        return requires;
    }

    public List<NpmDependency> getDependencies() {
        return dependencies;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isDevDependency() {
        return devDependency;
    }

    public Dependency getGraphDependency() {
        return dependency;
    }
}
