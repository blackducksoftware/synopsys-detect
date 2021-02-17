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

public class NpmProject {
    private final String name;
    private final String version;

    private final List<NpmRequires> declaredDevDependencies = new ArrayList<>();
    private final List<NpmRequires> declaredDependencies = new ArrayList<>();

    private final List<NpmDependency> resolvedDependencies = new ArrayList<>();

    public NpmProject(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void addAllDevDependencies(Collection<NpmRequires> requires) {
        this.declaredDevDependencies.addAll(requires);
    }

    public void addAllDependencies(Collection<NpmRequires> requires) {
        this.declaredDependencies.addAll(requires);
    }

    public void addAllResolvedDependencies(Collection<NpmDependency> resolvedDependencies) {
        this.resolvedDependencies.addAll(resolvedDependencies);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<NpmRequires> getDeclaredDependencies() {
        return declaredDependencies;
    }

    public List<NpmRequires> getDeclaredDevDependencies() {
        return declaredDevDependencies;
    }

    public List<NpmDependency> getResolvedDependencies() {
        return resolvedDependencies;
    }
}
