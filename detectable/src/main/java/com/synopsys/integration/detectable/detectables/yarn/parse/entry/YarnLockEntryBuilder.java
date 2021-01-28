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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;

public class YarnLockEntryBuilder {
    private final List<YarnLockEntryId> ids = new LinkedList<>();
    private String version;
    private final Map<String, YarnLockDependency> dependencies = new HashMap<>();

    public YarnLockEntryBuilder addId(YarnLockEntryId id) {
        ids.add(id);
        return this;
    }

    public YarnLockEntryBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public YarnLockEntryBuilder addDependency(YarnLockDependency dependency) {
        dependencies.put(dependency.getName(), dependency);
        return this;
    }

    public Map<String, YarnLockDependency> getDependencies() {
        return dependencies;
    }

    public Optional<YarnLockEntry> build() {
        if (ids.isEmpty() || version == null) {
            return Optional.empty();
        }
        return Optional.of(new YarnLockEntry(ids, version, new LinkedList<>(dependencies.values())));
    }
}
