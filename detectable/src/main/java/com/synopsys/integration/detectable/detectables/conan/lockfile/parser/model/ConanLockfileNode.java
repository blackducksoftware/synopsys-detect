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
package com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model;

import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ConanLockfileNode extends Stringable {
    private final String path;
    private final String ref;
    private final List<Integer> requires;

    @SerializedName("build_requires")
    private final List<Integer> buildRequires;

    @SerializedName("package_id")
    private final String packageId;

    @SerializedName("prev")
    private final String packageRevision;

    public ConanLockfileNode(String path, String ref, List<Integer> requires, List<Integer> buildRequires, String packageId, String packageRevision) {
        this.path = path;
        this.ref = ref;
        this.requires = requires;
        this.buildRequires = buildRequires;
        this.packageId = packageId;
        this.packageRevision = packageRevision;
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }

    public Optional<String> getRef() {
        return Optional.ofNullable(ref);
    }

    public Optional<List<Integer>> getRequires() {
        return Optional.ofNullable(requires);
    }

    public Optional<List<Integer>> getBuildRequires() {
        return Optional.ofNullable(buildRequires);
    }

    public Optional<String> getPackageId() {
        return Optional.ofNullable(packageId);
    }

    public Optional<String> getPackageRevision() {
        return Optional.ofNullable(packageRevision);
    }
}
