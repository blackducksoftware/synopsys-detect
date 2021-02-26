/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

    public boolean valid() {
        return !ids.isEmpty() && (version != null);
    }

    public YarnLockEntry build() {
        if (!valid()) {
            throw new UnsupportedOperationException("Attempted to build an incomplete yarn.lock entry");
        }
        return new YarnLockEntry(ids, version, new LinkedList<>(dependencies.values()));
    }

    public Optional<YarnLockEntry> buildIfValid() {
        if (!valid()) {
            return Optional.empty();
        }
        return Optional.of(new YarnLockEntry(ids, version, new LinkedList<>(dependencies.values())));
    }
}
